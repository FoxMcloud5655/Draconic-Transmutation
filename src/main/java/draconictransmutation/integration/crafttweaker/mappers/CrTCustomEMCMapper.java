package draconictransmutation.integration.crafttweaker.mappers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import draconictransmutation.DTCore;
import draconictransmutation.api.mapper.EMCMapper;
import draconictransmutation.api.mapper.IEMCMapper;
import draconictransmutation.api.mapper.collector.IMappingCollector;
import draconictransmutation.api.nss.NSSTag;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;

@EMCMapper(requiredMods = "crafttweaker")
public class CrTCustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final Map<NormalizedSimpleStack, Long> customEmcValues = new HashMap<>();

	public static void registerCustomEMC(@Nonnull NormalizedSimpleStack stack, long emcValue) {
		customEmcValues.put(stack, emcValue);
	}

	public static void unregisterNSS(@Nonnull NormalizedSimpleStack stack) {
		customEmcValues.remove(stack);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		for (Map.Entry<NormalizedSimpleStack, Long> entry : customEmcValues.entrySet()) {
			NormalizedSimpleStack normStack = entry.getKey();
			long value = entry.getValue();
			mapper.setValueBefore(normStack, value);
			if (normStack instanceof NSSTag) {
				//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
				((NSSTag) normStack).forEachElement(normalizedSimpleStack -> mapper.setValueBefore(normalizedSimpleStack, value));
			}
			DTCore.debugLog("CraftTweaker setting value for {} to {}", normStack, value);
		}
	}

	@Override
	public String getName() {
		return "CrTCustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Allows setting EMC values through CraftTweaker. This behaves similarly to if someone used the custom emc file instead.";
	}
}