package draconictransmutation.network;

import java.util.Optional;
import java.util.function.Function;

import draconictransmutation.DTCore;
import draconictransmutation.emc.EMCMappingHandler;
import draconictransmutation.network.packets.IPEPacket;
import draconictransmutation.network.packets.to_client.SyncEmcPKT;
import draconictransmutation.network.packets.to_client.SyncEmcPKT.EmcPKTInfo;
import draconictransmutation.network.packets.to_client.UpdateWindowIntPKT;
import draconictransmutation.network.packets.to_client.UpdateWindowLongPKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeClearPKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncChangePKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncEmcPKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncInputsAndLocksPKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncPKT;
import draconictransmutation.network.packets.to_server.KeyPressPKT;
import draconictransmutation.network.packets.to_server.SearchUpdatePKT;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public final class PacketHandler {

	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(DTCore.rl("main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	private static int index;

	public static void register() {
		//Client to server messages
		registerClientToServer(SearchUpdatePKT.class, SearchUpdatePKT::decode);
		registerClientToServer(KeyPressPKT.class, KeyPressPKT::decode);
		//Server to client messages
		registerServerToClient(KnowledgeClearPKT.class, KnowledgeClearPKT::decode);
		registerServerToClient(KnowledgeSyncPKT.class, KnowledgeSyncPKT::decode);
		registerServerToClient(KnowledgeSyncEmcPKT.class, KnowledgeSyncEmcPKT::decode);
		registerServerToClient(KnowledgeSyncInputsAndLocksPKT.class, KnowledgeSyncInputsAndLocksPKT::decode);
		registerServerToClient(KnowledgeSyncChangePKT.class, KnowledgeSyncChangePKT::decode);
		registerServerToClient(SyncEmcPKT.class, SyncEmcPKT::decode);
		registerServerToClient(UpdateWindowIntPKT.class, UpdateWindowIntPKT::decode);
		registerServerToClient(UpdateWindowLongPKT.class, UpdateWindowLongPKT::decode);
	}

	private static <MSG extends IPEPacket> void registerClientToServer(Class<MSG> type, Function<PacketBuffer, MSG> decoder) {
		registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
	}

	private static <MSG extends IPEPacket> void registerServerToClient(Class<MSG> type, Function<PacketBuffer, MSG> decoder) {
		registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
	}

	private static <MSG extends IPEPacket> void registerMessage(Class<MSG> type, Function<PacketBuffer, MSG> decoder, NetworkDirection networkDirection) {
		HANDLER.registerMessage(index++, type, IPEPacket::encode, decoder, IPEPacket::handle, Optional.of(networkDirection));
	}

	public static void sendProgressBarUpdateInt(IContainerListener listener, Container container, int propId, int propVal) {
		if (listener instanceof ServerPlayerEntity) {
			sendTo(new UpdateWindowIntPKT((short) container.windowId, (short) propId, propVal), (ServerPlayerEntity) listener);
		}
	}

	public static void sendProgressBarUpdateLong(IContainerListener listener, Container container, int propId, long propVal) {
		if (listener instanceof ServerPlayerEntity) {
			sendTo(new UpdateWindowLongPKT((short) container.windowId, (short) propId, propVal), (ServerPlayerEntity) listener);
		}
	}

	public static <MSG extends IPEPacket> void sendNonLocal(MSG msg, ServerPlayerEntity player) {
		if (player.server.isDedicatedServer() || !player.getGameProfile().getName().equals(player.server.getServerOwner())) {
			sendTo(msg, player);
		}
	}

	public static void sendFragmentedEmcPacket(ServerPlayerEntity player) {
		sendNonLocal(new SyncEmcPKT(serializeEmcData()), player);
	}

	public static void sendFragmentedEmcPacketToAll() {
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			SyncEmcPKT pkt = new SyncEmcPKT(serializeEmcData());
			for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
				sendNonLocal(pkt, player);
			}
		}
	}

	private static EmcPKTInfo[] serializeEmcData() {
		EmcPKTInfo[] data = EMCMappingHandler.createPacketData();
		//Simulate encoding the EMC packet to get an accurate size
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		int index = buf.writerIndex();
		new SyncEmcPKT(data).encode(buf);
		DTCore.debugLog("EMC data size: {} bytes", buf.writerIndex() - index);
		buf.release();
		return data;
	}

	/**
	 * Sends a packet to the server.<br> Must be called Client side.
	 */
	public static <MSG extends IPEPacket> void sendToServer(MSG msg) {
		HANDLER.sendToServer(msg);
	}

	/**
	 * Send a packet to a specific player.<br> Must be called Server side.
	 */
	public static <MSG extends IPEPacket> void sendTo(MSG msg, ServerPlayerEntity player) {
		if (!(player instanceof FakePlayer)) {
			HANDLER.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}