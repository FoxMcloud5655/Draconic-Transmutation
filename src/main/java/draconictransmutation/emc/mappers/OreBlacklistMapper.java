package draconictransmutation.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import draconictransmutation.api.mapper.EMCMapper;
import draconictransmutation.api.mapper.IEMCMapper;
import draconictransmutation.api.mapper.collector.IMappingCollector;
import draconictransmutation.api.nss.NSSItem;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import net.minecraft.item.Item;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

@EMCMapper
public class OreBlacklistMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final ResourceLocation ORES = new ResourceLocation("forge", "ores");

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		//Note: We need to get the tag by resource location, as named tags are not populated yet here
		ITag<Item> ores = dataPackRegistries.func_244358_d().getItemTags().get(ORES);
		if (ores != null) {
			for (Item ore : ores.getAllElements()) {
				NSSItem nssOre = NSSItem.createItem(ore);
				mapper.setValueBefore(nssOre, 0L);
				mapper.setValueAfter(nssOre, 0L);
			}
		}
	}

	@Override
	public String getName() {
		return "OresBlacklistMapper";
	}

	@Override
	public String getDescription() {
		return "Set EMC=0 for everything in the forge:ores tag";
	}
}