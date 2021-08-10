package draconictransmutation.network.packets.to_client.knowledge;

import draconictransmutation.DTCore;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KnowledgeSyncPKT implements IPEPacket {

	private final CompoundNBT nbt;

	public KnowledgeSyncPKT(CompoundNBT nbt) {
		this.nbt = nbt;
	}

	@Override
	public void handle(Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(cap -> cap.deserializeNBT(nbt));
		}
		DTCore.debugLog("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeCompoundTag(nbt);
	}

	public static KnowledgeSyncPKT decode(PacketBuffer buffer) {
		return new KnowledgeSyncPKT(buffer.readCompoundTag());
	}
}