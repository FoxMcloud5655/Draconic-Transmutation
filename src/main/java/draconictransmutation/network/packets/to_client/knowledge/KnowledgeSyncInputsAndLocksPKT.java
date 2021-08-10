package draconictransmutation.network.packets.to_client.knowledge;

import java.util.HashMap;
import java.util.Map;

import draconictransmutation.DTCore;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import draconictransmutation.gameObjs.container.TransmutationContainer;
import draconictransmutation.gameObjs.container.inventory.TransmutationInventory;
import draconictransmutation.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KnowledgeSyncInputsAndLocksPKT implements IPEPacket {

	private final Map<Integer, ItemStack> stacksToSync;
	private final TargetUpdateType updateTargets;

	public KnowledgeSyncInputsAndLocksPKT(Map<Integer, ItemStack> stacksToSync, TargetUpdateType updateTargets) {
		this.stacksToSync = stacksToSync;
		this.updateTargets = updateTargets;
	}

	@Override
	public void handle(Context context) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(cap -> {
				cap.receiveInputsAndLocks(stacksToSync);
				if (updateTargets != TargetUpdateType.NONE && player.openContainer instanceof TransmutationContainer) {
					//Update targets in case total available EMC is now different
					TransmutationInventory transmutationInventory = ((TransmutationContainer) player.openContainer).transmutationInventory;
					if (updateTargets == TargetUpdateType.ALL) {
						transmutationInventory.updateClientTargets();
					} else {//If needed
						transmutationInventory.checkForUpdates();
					}
				}
			});
		}
		DTCore.debugLog("** RECEIVED TRANSMUTATION INPUT AND LOCK DATA CLIENTSIDE **");
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeVarInt(stacksToSync.size());
		for (Map.Entry<Integer, ItemStack> entry : stacksToSync.entrySet()) {
			buffer.writeVarInt(entry.getKey());
			buffer.writeItemStack(entry.getValue());
		}
		buffer.writeEnumValue(updateTargets);
	}

	public static KnowledgeSyncInputsAndLocksPKT decode(PacketBuffer buffer) {
		int size = buffer.readVarInt();
		Map<Integer, ItemStack> syncedStacks = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			syncedStacks.put(buffer.readVarInt(), buffer.readItemStack());
		}
		return new KnowledgeSyncInputsAndLocksPKT(syncedStacks, buffer.readEnumValue(TargetUpdateType.class));
	}
}