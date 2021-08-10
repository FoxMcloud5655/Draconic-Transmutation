package draconictransmutation.gameObjs;

import draconictransmutation.DTCore;
import draconictransmutation.integration.IntegrationHelper;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class DTTags {
	public static class Items {
		/**
		 * Items in this tag can have their NBT tags duped by transmutation tables
		 */
		public static final INamedTag<Item> NBT_WHITELIST = tag("nbt_whitelist");
		private static INamedTag<Item> tag(String name) {
			return ItemTags.makeWrapperTag(DTCore.rl(name).toString());
		}
		private static INamedTag<Item> curiosTag(String name) {
			return ItemTags.makeWrapperTag(new ResourceLocation(IntegrationHelper.CURIO_MODID, name).toString());
		}
	}
}