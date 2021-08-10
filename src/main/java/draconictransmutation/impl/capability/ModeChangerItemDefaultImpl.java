package draconictransmutation.impl.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import draconictransmutation.api.capabilities.item.IModeChanger;
import draconictransmutation.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public final class ModeChangerItemDefaultImpl implements IModeChanger {

	@Override
	public byte getMode(@Nonnull ItemStack stack) {
		return stack.hasTag() ? stack.getOrCreateTag().getByte(Constants.NBT_KEY_MODE) : 0;
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, @Nullable Hand hand) {
		return false;
	}
}