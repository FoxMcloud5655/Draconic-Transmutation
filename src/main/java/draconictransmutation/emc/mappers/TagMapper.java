package draconictransmutation.emc.mappers;

import java.util.Collections;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import draconictransmutation.api.mapper.IEMCMapper;
import draconictransmutation.api.mapper.collector.IMappingCollector;
import draconictransmutation.api.nss.AbstractNSSTag;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;

public class TagMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		AbstractNSSTag.getAllCreatedTags().forEach(stack -> stack.forEachElement(normalizedSimpleStack -> {
			//Tag -> element
			mapper.addConversion(1, stack, Collections.singletonList(normalizedSimpleStack));
			//Element -> tag
			mapper.addConversion(1, normalizedSimpleStack, Collections.singletonList(stack));
		}));
	}

	@Override
	public String getName() {
		return "TagMapper";
	}

	@Override
	public String getDescription() {
		return "Adds back and forth conversions of objects and their Tag variant. (EMC values assigned to tags will not behave properly if this mapper is disabled)";
	}
}