package draconictransmutation.gameObjs.container.slots.transmutation;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import draconictransmutation.gameObjs.container.inventory.TransmutationInventory;
import draconictransmutation.utils.EMCHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotConsume extends SlotItemHandler {

	private final TransmutationInventory inv;

	public SlotConsume(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		if (inv.isServer() && !stack.isEmpty()) {
			inv.handleKnowledge(stack);
			inv.addEmc(BigInteger.valueOf(EMCHelper.getEmcSellValue(stack)).multiply(BigInteger.valueOf(stack.getCount())));
			this.onSlotChanged();
		}
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return EMCHelper.doesItemHaveEmc(stack);
	}
}