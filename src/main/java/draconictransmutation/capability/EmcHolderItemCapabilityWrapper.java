package draconictransmutation.capability;

import javax.annotation.Nonnull;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.item.IItemEmcHolder;
import draconictransmutation.api.capabilities.tile.IEmcStorage.EmcAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class EmcHolderItemCapabilityWrapper extends BasicItemCapability<IItemEmcHolder> implements IItemEmcHolder {

	@Override
	public Capability<IItemEmcHolder> getCapability() {
		return ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY;
	}

	@Override
	public long insertEmc(@Nonnull ItemStack stack, long toInsert, EmcAction action) {
		return getItem().insertEmc(stack, toInsert, action);
	}

	@Override
	public long extractEmc(@Nonnull ItemStack stack, long toExtract, EmcAction action) {
		return getItem().extractEmc(stack, toExtract, action);
	}

	@Override
	public long getStoredEmc(@Nonnull ItemStack stack) {
		return getItem().getStoredEmc(stack);
	}

	@Override
	public long getMaximumEmc(@Nonnull ItemStack stack) {
		return getItem().getMaximumEmc(stack);
	}
}