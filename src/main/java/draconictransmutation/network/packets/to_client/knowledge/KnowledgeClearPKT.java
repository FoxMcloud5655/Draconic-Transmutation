package draconictransmutation.network.packets.to_client.knowledge;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.IKnowledgeProvider;
import draconictransmutation.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KnowledgeClearPKT implements IPEPacket {

	@Override
	public void handle(Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(IKnowledgeProvider::clearKnowledge);
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
	}

	public static KnowledgeClearPKT decode(PacketBuffer buffer) {
		return new KnowledgeClearPKT();
	}
}