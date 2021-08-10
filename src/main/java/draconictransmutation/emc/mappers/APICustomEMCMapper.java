package draconictransmutation.emc.mappers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import draconictransmutation.DTCore;
import draconictransmutation.api.imc.CustomEMCRegistration;
import draconictransmutation.api.mapper.EMCMapper;
import draconictransmutation.api.mapper.IEMCMapper;
import draconictransmutation.api.mapper.collector.IMappingCollector;
import draconictransmutation.api.nss.NSSItem;
import draconictransmutation.api.nss.NSSTag;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import draconictransmutation.emc.EMCMappingHandler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;

@EMCMapper
public class APICustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@EMCMapper.Instance
	public static final APICustomEMCMapper INSTANCE = new APICustomEMCMapper();
	private static final int PRIORITY_MIN_VALUE = 0;
	private static final int PRIORITY_MAX_VALUE = 512;
	private static final int PRIORITY_DEFAULT_VALUE = 1;

	private APICustomEMCMapper() {
	}

	private final Map<String, Map<NormalizedSimpleStack, Long>> customEMCforMod = new HashMap<>();

	public void registerCustomEMC(String modid, CustomEMCRegistration customEMCRegistration) {
		NormalizedSimpleStack stack = customEMCRegistration.getStack();
		if (stack == null) {
			return;
		}
		long emcValue = customEMCRegistration.getValue();
		if (emcValue < 0) {
			emcValue = 0;
		}
		DTCore.debugLog("Mod: '{}' registered a custom EMC value of: '{}' for the NormalizedSimpleStack: '{}'", modid, emcValue, stack);
		customEMCforMod.computeIfAbsent(modid, k -> new HashMap<>()).put(stack, emcValue);
	}

	@Override
	public String getName() {
		return "APICustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Allows other mods to easily set EMC values using the ProjectEAPI";
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		Map<String, Integer> priorityMap = new HashMap<>();

		for (String modId : customEMCforMod.keySet()) {
			String configKey = getName() + ".priority." + (modId == null ? "__no_modid" : modId);
			int priority = EMCMappingHandler.getOrSetDefault(config, configKey, "Priority for this mod", PRIORITY_DEFAULT_VALUE);
			priorityMap.put(modId, priority);
		}

		List<String> modIds = new ArrayList<>(customEMCforMod.keySet());
		modIds.sort(Comparator.comparingInt((ToIntFunction<String>) priorityMap::get).reversed());

		for (String modId : modIds) {
			String modIdOrUnknown = modId == null ? "unknown mod" : modId;
			if (customEMCforMod.containsKey(modId)) {
				for (Map.Entry<NormalizedSimpleStack, Long> entry : customEMCforMod.get(modId).entrySet()) {
					NormalizedSimpleStack normStack = entry.getKey();
					long emc = entry.getValue();
					if (isAllowedToSet(modId, normStack, emc, config)) {
						mapper.setValueBefore(normStack, emc);
						if (normStack instanceof NSSTag) {
							//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
							((NSSTag) normStack).forEachElement(normalizedSimpleStack -> mapper.setValueBefore(normalizedSimpleStack, emc));
						}
						DTCore.debugLog("{} setting value for {} to {}", modIdOrUnknown, normStack, emc);
					} else {
						DTCore.debugLog("Disallowed {} to set the value for {} to {}", modIdOrUnknown, normStack, emc);
					}
				}
			}
		}
	}

	private boolean isAllowedToSet(String modId, NormalizedSimpleStack stack, Long value, CommentedFileConfig config) {
		String resourceLocation;
		if (stack instanceof NSSItem) {
			//Allow both item names and tag locations
			resourceLocation = ((NSSItem) stack).getResourceLocation().toString();
		} else {
			resourceLocation = "IntermediateFakeItemsUsedInRecipes:";
		}
		String modForItem = resourceLocation.substring(0, resourceLocation.indexOf(':'));
		String configPath = String.format("permissions.%s.%s", modId, modForItem);
		String comment = String.format("Allow mod '%s' to set and or remove values for mod '%s'. Options: [both, set, remove, none]", modId, modForItem);
		String permission = EMCMappingHandler.getOrSetDefault(config, configPath, comment, "both");
		if (permission.equals("both")) {
			return true;
		}
		if (value == 0) {
			return permission.equals("remove");
		}
		return permission.equals("set");
	}
}