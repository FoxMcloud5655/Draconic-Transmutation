package draconictransmutation.events;

import java.util.Optional;

import draconictransmutation.DTCore;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.item.IItemEmcHolder;
import draconictransmutation.config.DTConfig;
import draconictransmutation.utils.Constants;
import draconictransmutation.utils.EMCHelper;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DTCore.MODID, value = Dist.CLIENT)
public class ToolTipEvent {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event) {
		ItemStack current = event.getItemStack();
		if (current.isEmpty()) {
			return;
		}
		PlayerEntity clientPlayer = Minecraft.getInstance().player;
		if (DTConfig.client.tagToolTips.get()) {
			for (ResourceLocation tag : ItemTags.getCollection().getOwningTags(current.getItem())) {
				event.getToolTip().add(new StringTextComponent("#" + tag));
			}
		}

		if (DTConfig.client.emcToolTips.get() && (!DTConfig.client.shiftEmcToolTips.get() || Screen.hasShiftDown())) {
			long value = EMCHelper.getEmcValue(current);
			if (value > 0) {
				event.getToolTip().add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					event.getToolTip().add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
				if (Screen.hasShiftDown() && clientPlayer != null && clientPlayer.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).map(k -> k.hasKnowledge(current)).orElse(false)) {
					event.getToolTip().add(DTLang.EMC_HAS_KNOWLEDGE.translateColored(TextFormatting.YELLOW));
				}
			}
		}

		if (current.hasTag()) {
			long value;
			CompoundNBT tag = current.getOrCreateTag();
			if (tag.contains(Constants.NBT_KEY_STORED_EMC, NBT.TAG_LONG)) {
				value = tag.getLong(Constants.NBT_KEY_STORED_EMC);
			} else {
				Optional<IItemEmcHolder> holderCapability = current.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if (holderCapability.isPresent()) {
					value = holderCapability.get().getStoredEmc(current);
				} else {
					return;
				}
			}
			event.getToolTip().add(DTLang.EMC_STORED.translateColored(TextFormatting.YELLOW, TextFormatting.WHITE, Constants.EMC_FORMATTER.format(value)));
		}
	}
}