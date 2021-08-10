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
import draconictransmutation.utils.MathUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotInput extends SlotItemHandler {

	private final TransmutationInventory inv;

	public SlotInput(TransmutationInventory inv, int index, int x, int y) {
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
			inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.IF_NEEDED);
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
					//Get the emc that the inventory has that is not in any stars
					long shrunkenAvailableEMC = MathUtils.clampToLong(inv.provider.getEmc());
					//try to insert it
					long actualInserted = emcHolder.insertEmc(stack, shrunkenAvailableEMC, EmcAction.EXECUTE);
					if (actualInserted > 0) {
						//if we actually managed to insert some sync the slots changed, but don't update targets
						// as that will be done by removing the emc and syncing how much is stored there
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
						inv.removeEmc(BigInteger.valueOf(actualInserted));
					} else if (emcHolder.getStoredEmc(stack) > 0) {
						//If we didn't manage to insert any into our star, and we do have emc stored
						// update the targets
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
					} else {
						//If we didn't manage to insert any into our star, and we don't have any emc stored
						// don't bother updating the targets
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
					}
				} else {
					//Update the fact the slots changed but don't bother updating targets
					inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
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