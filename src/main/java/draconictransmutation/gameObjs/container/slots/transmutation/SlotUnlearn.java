package draconictransmutation.gameObjs.container.slots.transmutation;

import javax.annotation.Nonnull;

import draconictransmutation.gameObjs.container.inventory.TransmutationInventory;
import draconictransmutation.utils.EMCHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotUnlearn extends SlotItemHandler {

	private final TransmutationInventory inv;

	public SlotUnlearn(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return !this.getHasStack() && (EMCHelper.doesItemHaveEmc(stack));
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		if (inv.isServer() && !stack.isEmpty()) {
			inv.handleUnlearn(stack.copy());
		}
		super.putStack(stack);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {
		return 1;
	}
}