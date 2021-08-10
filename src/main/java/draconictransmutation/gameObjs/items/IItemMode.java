package draconictransmutation.gameObjs.items;

import javax.annotation.Nonnull;

import draconictransmutation.api.capabilities.item.IModeChanger;
import draconictransmutation.utils.Constants;
import draconictransmutation.utils.text.DTLang;
import draconictransmutation.utils.text.ILangEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public interface IItemMode extends IModeChanger {

	ILangEntry[] getModeLangEntries();

	default byte getModeCount() {
		return (byte) getModeLangEntries().length;
	}

	default ILangEntry getModeLangEntry(ItemStack stack) {
		ILangEntry[] langEntries = getModeLangEntries();
		byte mode = getMode(stack);
		if (mode < 0 || mode >= langEntries.length) {
			return DTLang.INVALID_MODE;
		}
		return langEntries[mode];
	}

	@Override
	default byte getMode(@Nonnull ItemStack stack) {
		return stack.hasTag() ? stack.getOrCreateTag().getByte(Constants.NBT_KEY_MODE) : 0;
	}

	@Override
	default boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		byte numModes = getModeCount();
		if (numModes < 2) {
			//If we have no modes or we are set to the only mode fail
			return false;
		}
		//Update the mode
		stack.getOrCreateTag().putByte(Constants.NBT_KEY_MODE, (byte) ((getMode(stack) + 1) % numModes));
		player.sendMessage(getModeSwitchEntry().translate(getModeLangEntry(stack)), Util.DUMMY_UUID);
		return true;
	}

	default ILangEntry getModeSwitchEntry() {
		return DTLang.MODE_SWITCH;
	}

	default ITextComponent getToolTip(ItemStack stack) {
		return DTLang.CURRENT_MODE.translate(TextFormatting.AQUA, getModeLangEntry(stack));
	}
}