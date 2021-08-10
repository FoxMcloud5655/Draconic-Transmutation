package draconictransmutation.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.item.IExtraFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;

public class ExtraFunctionItemCapabilityWrapper extends BasicItemCapability<IExtraFunction> implements IExtraFunction {

	@Override
	public Capability<IExtraFunction> getCapability() {
		return ProjectEAPI.EXTRA_FUNCTION_ITEM_CAPABILITY;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nullable Hand hand) {
		return getItem().doExtraFunction(stack, player, hand);
	}
}