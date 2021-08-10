package draconictransmutation.network.packets.to_server;

import java.util.Optional;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.config.DTConfig;
import draconictransmutation.handlers.InternalAbilities;
import draconictransmutation.network.packets.IPEPacket;
import draconictransmutation.utils.DTKeybind;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KeyPressPKT implements IPEPacket {

	private final DTKeybind key;

	public KeyPressPKT(DTKeybind key) {
		this.key = key;
	}

	@Override
	public void handle(Context context) {
		ServerPlayerEntity player = context.getSender();
		if (player == null) {
			return;
		}
		Optional<InternalAbilities> cap = player.getCapability(InternalAbilities.CAPABILITY).resolve();
		if (!cap.isPresent()) {
			return;
		}
		InternalAbilities internalAbilities = cap.get();
		for (Hand hand : Hand.values()) {
			ItemStack stack = player.getHeldItem(hand);
			switch (key) {
				case CHARGE:
					if (tryPerformCapability(stack, ProjectEAPI.CHARGE_ITEM_CAPABILITY, capability -> capability.changeCharge(player, stack, hand))) {
						return;
					}
					break;
				case EXTRA_FUNCTION:
					if (tryPerformCapability(stack, ProjectEAPI.EXTRA_FUNCTION_ITEM_CAPABILITY, capability -> capability.doExtraFunction(stack, player, hand))) {
						return;
					}
					break;
				case MODE:
					if (tryPerformCapability(stack, ProjectEAPI.MODE_CHANGER_ITEM_CAPABILITY, capability -> capability.changeMode(player, stack, hand))) {
						return;
					}
					break;
			}
		}
	}

	private static <CAPABILITY> boolean tryPerformCapability(ItemStack stack, Capability<CAPABILITY> capability, NonNullPredicate<CAPABILITY> perform) {
		return !stack.isEmpty() && stack.getCapability(capability).filter(perform).isPresent();
	}

	private static boolean isSafe(ItemStack stack) {
		return DTConfig.server.misc.unsafeKeyBinds.get() || stack.isEmpty();
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeEnumValue(key);
	}

	public static KeyPressPKT decode(PacketBuffer buf) {
		return new KeyPressPKT(buf.readEnumValue(DTKeybind.class));
	}
}