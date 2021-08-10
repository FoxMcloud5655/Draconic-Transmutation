package draconictransmutation.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import draconictransmutation.DTCore;
import draconictransmutation.api.mapper.EMCMapper;
import draconictransmutation.api.mapper.IEMCMapper;
import draconictransmutation.api.mapper.collector.IMappingCollector;
import draconictransmutation.api.nss.NSSTag;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import draconictransmutation.config.CustomEMCParser;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;

@EMCMapper
public class CustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		for (CustomEMCParser.CustomEMCEntry entry : CustomEMCParser.currentEntries.entries) {
			DTCore.debugLog("Adding custom EMC value for {}: {}", entry.item, entry.emc);
			mapper.setValueBefore(entry.item, entry.emc);
			if (entry.item instanceof NSSTag) {
				//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
				((NSSTag) entry.item).forEachElement(normalizedSimpleStack -> mapper.setValueBefore(normalizedSimpleStack, entry.emc));
			}
		}
	}

	@Override
	public String getName() {
		return "CustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Uses the `custom_emc.json` File to add EMC values.";
	}
}