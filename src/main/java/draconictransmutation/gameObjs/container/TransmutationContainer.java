package draconictransmutation.gameObjs.container;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import draconictransmutation.gameObjs.container.inventory.TransmutationInventory;
import draconictransmutation.gameObjs.container.slots.transmutation.SlotConsume;
import draconictransmutation.gameObjs.container.slots.transmutation.SlotInput;
import draconictransmutation.gameObjs.container.slots.transmutation.SlotLock;
import draconictransmutation.gameObjs.container.slots.transmutation.SlotOutput;
import draconictransmutation.gameObjs.container.slots.transmutation.SlotUnlearn;
import draconictransmutation.gameObjs.registries.DTContainerTypes;
import draconictransmutation.network.PacketHandler;
import draconictransmutation.network.packets.to_server.SearchUpdatePKT;
import draconictransmutation.utils.ContainerHelper;
import draconictransmutation.utils.EMCHelper;
import draconictransmutation.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TransmutationContainer extends Container {

	public final TransmutationInventory transmutationInventory;
	private final int blocked;

	public static TransmutationContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf) {
		return new TransmutationContainer(windowId, invPlayer, buf.readEnumValue(Hand.class));
	}

	public TransmutationContainer(int windowId, PlayerInventory invPlayer, Hand hand) {
		super(DTContainerTypes.TRANSMUTATION_CONTAINER.get(), windowId);
		this.transmutationInventory = new TransmutationInventory(invPlayer.player);

		// Transmutation Inventory
		this.addSlot(new SlotInput(transmutationInventory, 0, 43, 23));
		this.addSlot(new SlotInput(transmutationInventory, 1, 34, 41));
		this.addSlot(new SlotInput(transmutationInventory, 2, 52, 41));
		this.addSlot(new SlotInput(transmutationInventory, 3, 16, 50));
		this.addSlot(new SlotInput(transmutationInventory, 4, 70, 50));
		this.addSlot(new SlotInput(transmutationInventory, 5, 34, 59));
		this.addSlot(new SlotInput(transmutationInventory, 6, 52, 59));
		this.addSlot(new SlotInput(transmutationInventory, 7, 43, 77));
		this.addSlot(new SlotLock(transmutationInventory, 8, 158, 50));
		this.addSlot(new SlotConsume(transmutationInventory, 9, 107, 97));
		this.addSlot(new SlotUnlearn(transmutationInventory, 10, 89, 97));
		this.addSlot(new SlotOutput(transmutationInventory, 11, 158, 9));
		this.addSlot(new SlotOutput(transmutationInventory, 12, 176, 13));
		this.addSlot(new SlotOutput(transmutationInventory, 13, 193, 30));
		this.addSlot(new SlotOutput(transmutationInventory, 14, 199, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 15, 193, 70));
		this.addSlot(new SlotOutput(transmutationInventory, 16, 176, 87));
		this.addSlot(new SlotOutput(transmutationInventory, 17, 158, 91));
		this.addSlot(new SlotOutput(transmutationInventory, 18, 140, 87));
		this.addSlot(new SlotOutput(transmutationInventory, 19, 123, 70));
		this.addSlot(new SlotOutput(transmutationInventory, 20, 116, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 21, 123, 30));
		this.addSlot(new SlotOutput(transmutationInventory, 22, 140, 13));
		this.addSlot(new SlotOutput(transmutationInventory, 23, 158, 31));
		this.addSlot(new SlotOutput(transmutationInventory, 24, 177, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 25, 158, 69));
		this.addSlot(new SlotOutput(transmutationInventory, 26, 139, 50));

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 35, 117);

		blocked = hand == Hand.MAIN_HAND ? (inventorySlots.size() - 1) - (8 - invPlayer.currentItem) : -1;
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int slotIndex) {
		if (slotIndex <= 7) {
			//Input Slots
			return ItemStack.EMPTY;
		}
		Slot slot = this.getSlot(slotIndex);
		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		if (slotIndex >= 11 && slotIndex <= 26) {
			// Output Slots
			long itemEmc = EMCHelper.getEmcValue(newStack);
			//Double check the item actually has Emc and something didn't just go terribly wrong
			if (itemEmc > 0) {
				//Note: We can just set the size here as newStack is a copy stack used for modifications
				newStack.setCount(newStack.getMaxStackSize());
				//Check how much we can fit of the stack
				int stackSize = newStack.getCount() - ItemHelper.simulateFit(player.inventory.mainInventory, newStack);
				if (stackSize > 0) {
					BigInteger availableEMC = transmutationInventory.getAvailableEmc();
					BigInteger emc = BigInteger.valueOf(itemEmc);
					BigInteger totalEmc = emc.multiply(BigInteger.valueOf(stackSize));
					if (totalEmc.compareTo(availableEMC) > 0) {
						//We need more EMC than we have available so we have to calculate how much we actually can produce
						//Note: We first multiply then compare, as the larger the numbers are the less efficient division becomes
						BigInteger numOperations = availableEMC.divide(emc);
						//Note: Uses intValueExact as we already compared to a multiplication of an int times the number we divided by,
						// so it should fit into an int
						stackSize = numOperations.intValueExact();
						totalEmc = emc.multiply(numOperations);
						if (stackSize <= 0) {
							return ItemStack.EMPTY;
						}
					}
					//Set the stack size to what we found the max value is we have room for (capped at the stack's own max size)
					newStack.setCount(stackSize);
					IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
					if (transmutationInventory.isServer()) {
						transmutationInventory.removeEmc(totalEmc);
					}
					ItemHandlerHelper.insertItemStacked(inv, newStack, false);
				}
			}
		} else if (slotIndex > 26) {
			long emc = EMCHelper.getEmcSellValue(stack);
			if (emc == 0) {
				return ItemStack.EMPTY;
			}
			if (transmutationInventory.isServer()) {
				BigInteger emcBigInt = BigInteger.valueOf(emc);
				transmutationInventory.handleKnowledge(newStack);
				transmutationInventory.addEmc(emcBigInt.multiply(BigInteger.valueOf(stack.getCount())));
			}
			slot.putStack(ItemStack.EMPTY);
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int dragType, @Nonnull ClickType clickType, @Nonnull PlayerEntity player) {
		if (slot == blocked || clickType == ClickType.SWAP && dragType == 40 && blocked == -1) {
			return ItemStack.EMPTY;
		}
		if (player.getEntityWorld().isRemote && transmutationInventory.getHandlerForSlot(slot) == transmutationInventory.outputs) {
			PacketHandler.sendToServer(new SearchUpdatePKT(transmutationInventory.getIndexFromSlot(slot), getSlot(slot).getStack()));
		}
		return super.slotClick(slot, dragType, clickType, player);
	}

	@Override
	public boolean canDragIntoSlot(@Nonnull Slot slot) {
		return !(slot instanceof SlotConsume || slot instanceof SlotUnlearn || slot instanceof SlotInput || slot instanceof SlotLock || slot instanceof SlotOutput);
	}
}