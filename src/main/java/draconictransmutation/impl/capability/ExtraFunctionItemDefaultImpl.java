package draconictransmutation.impl.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import draconictransmutation.api.capabilities.item.IExtraFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public final class ExtraFunctionItemDefaultImpl implements IExtraFunction {

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nullable Hand hand) {
		return false;
	}
}