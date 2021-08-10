package draconictransmutation.impl.capability;

import javax.annotation.Nonnull;

import draconictransmutation.api.capabilities.item.IItemEmcHolder;
import draconictransmutation.api.capabilities.tile.IEmcStorage.EmcAction;
import draconictransmutation.gameObjs.items.ItemPE;
import net.minecraft.item.ItemStack;

public final class EmcHolderItemDefaultImpl implements IItemEmcHolder {

	@Override
	public long insertEmc(@Nonnull ItemStack stack, long toInsert, EmcAction action) {
		if (toInsert < 0) {
			return extractEmc(stack, -toInsert, action);
		}
		long toAdd = Math.min(getNeededEmc(stack), toInsert);
		if (action.execute()) {
			ItemPE.addEmcToStack(stack, toAdd);
		}
		return toAdd;
	}

	@Override
	public long extractEmc(@Nonnull ItemStack stack, long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return insertEmc(stack, -toExtract, action);
		}
		long storedEmc = getStoredEmc(stack);
		long toRemove = Math.min(storedEmc, toExtract);
		if (action.execute()) {
			ItemPE.setEmc(stack, storedEmc - toRemove);
		}
		return toRemove;
	}

	@Override
	public long getStoredEmc(@Nonnull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}

	@Override
	public long getMaximumEmc(@Nonnull ItemStack stack) {
		return 1;
	}
}