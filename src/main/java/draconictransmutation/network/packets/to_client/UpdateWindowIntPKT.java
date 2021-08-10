package draconictransmutation.network.packets.to_client;

import draconictransmutation.gameObjs.container.DTContainer;
import draconictransmutation.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

// Version of SWindowPropertyPacket that does not truncate the `value` arg to a short
public class UpdateWindowIntPKT implements IPEPacket {

	private final short windowId;
	private final short propId;
	private final int propVal;

	public UpdateWindowIntPKT(short windowId, short propId, int propVal) {
		this.windowId = windowId;
		this.propId = propId;
		this.propVal = propVal;
	}

	@Override
	public void handle(Context context) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null && player.openContainer instanceof DTContainer && player.openContainer.windowId == windowId) {
			((DTContainer) player.openContainer).updateProgressBarInt(propId, propVal);
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeShort(windowId);
		buffer.writeShort(propId);
		buffer.writeVarInt(propVal);
	}

	public static UpdateWindowIntPKT decode(PacketBuffer buffer) {
		return new UpdateWindowIntPKT(buffer.readShort(), buffer.readShort(), buffer.readVarInt());
	}
}