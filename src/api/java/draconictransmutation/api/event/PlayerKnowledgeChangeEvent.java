package draconictransmutation.api.event;

import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired serverside after a players transmutation knowledge is changed
 *
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}, and has no result
 *
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 */
public class PlayerKnowledgeChangeEvent extends Event {

	private final UUID playerUUID;

	public PlayerKnowledgeChangeEvent(@Nonnull PlayerEntity player) {
		this(player.getUniqueID());
	}

	public PlayerKnowledgeChangeEvent(@Nonnull UUID playerUUID) {
		this.playerUUID = playerUUID;
	}

	/**
	 * @return The player UUID whose knowledge changed. The associated player may or may not be logged in when this event fires.
	 */
	@Nonnull
	public UUID getPlayerUUID() {
		return playerUUID;
	}
}