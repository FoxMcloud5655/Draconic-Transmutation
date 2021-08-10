package draconictransmutation.network.packets.to_client.knowledge;

import draconictransmutation.DTCore;
import draconictransmutation.api.ItemInfo;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.gameObjs.container.TransmutationContainer;
import draconictransmutation.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KnowledgeSyncChangePKT implements IPEPacket {

	private final ItemInfo change;
	private final boolean learned;

	public KnowledgeSyncChangePKT(ItemInfo change, boolean learned) {
		this.change = change;
		this.learned = learned;
	}

	@Override
	public void handle(Context context) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(cap -> {
				if (learned) {
					if (!cap.hasKnowledge(change) && cap.addKnowledge(change) && player.openContainer instanceof TransmutationContainer) {
						((TransmutationContainer) player.openContainer).transmutationInventory.itemLearned();
					}
				} else if (cap.hasKnowledge(change) && cap.removeKnowledge(change) && player.openContainer instanceof TransmutationContainer) {
					((TransmutationContainer) player.openContainer).transmutationInventory.itemUnlearned();
				}
			});
		}
		DTCore.debugLog("** RECEIVED TRANSMUTATION KNOWLEDGE CHANGE DATA CLIENTSIDE **");
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeRegistryId(change.getItem());
		buffer.writeCompoundTag(change.getNBT());
		buffer.writeBoolean(learned);
	}

	public static KnowledgeSyncChangePKT decode(PacketBuffer buffer) {
		return new KnowledgeSyncChangePKT(ItemInfo.fromItem(buffer.readRegistryId(), buffer.readCompoundTag()), buffer.readBoolean());
	}
}