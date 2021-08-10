package draconictransmutation.gameObjs.container.slots.transmutation;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nonnull;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import draconictransmutation.api.capabilities.item.IItemEmcHolder;
import draconictransmutation.api.capabilities.tile.IEmcStorage.EmcAction;
import draconictransmutation.gameObjs.container.inventory.TransmutationInventory;
import draconictransmutation.utils.EMCHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotLock extends SlotItemHandler {

	private final TransmutationInventory inv;

	public SlotLock(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		ItemStack stack = super.decrStackSize(amount);
		//Decrease the size of the stack
		if (!stack.isEmpty() && inv.isServer()) {
			//Sync the change to the client
			inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
		}
		return stack;
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		super.putStack(stack);
		if (inv.isServer()) {
			if (stack.isEmpty()) {
				inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
			} else {
				if (EMCHelper.doesItemHaveEmc(stack)) {
					inv.handleKnowledge(stack);
				}
				Optional<IItemEmcHolder> capability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if (capability.isPresent()) {
					IItemEmcHolder emcHolder = capability.get();
					long actualExtracted = emcHolder.extractEmc(stack, emcHolder.getStoredEmc(stack), EmcAction.EXECUTE);
					if (actualExtracted > 0) {
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
						inv.addEmc(BigInteger.valueOf(actualExtracted));
					} else {
						//If we didn't move any EMC into the inventory we still need to sync the fact the slot changed so to update targets
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
					}
				} else {
					//If there is no capability we still need to sync the change
					inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
				}
			}
		}
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