package draconictransmutation.events;

import draconictransmutation.DTCore;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.capability.managing.BasicCapabilityResolver;
import draconictransmutation.handlers.InternalAbilities;
import draconictransmutation.handlers.InternalTimers;
import draconictransmutation.impl.TransmutationOffline;
import draconictransmutation.impl.capability.KnowledgeImpl;
import draconictransmutation.network.PacketHandler;
import draconictransmutation.utils.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

@Mod.EventBusSubscriber(modid = DTCore.MODID)
public class PlayerEvents {

	// On death or return from end, copy the capability data
	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone evt) {
		evt.getOriginal().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(old -> {
			CompoundNBT knowledge = old.serializeNBT();
			evt.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(c -> c.deserializeNBT(knowledge));
		});
	}

	// On death or return from end, sync to the client
	@SubscribeEvent
	public static void respawnEvent(PlayerEvent.PlayerRespawnEvent evt) {
		evt.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(c -> c.sync((ServerPlayerEntity) evt.getPlayer()));
	}

	@SubscribeEvent
	public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		event.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(c -> c.sync((ServerPlayerEntity) event.getPlayer()));
	}

	@SubscribeEvent
	public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
		if (evt.getObject() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) evt.getObject();
			attachCapability(evt, KnowledgeImpl.Provider.NAME, new KnowledgeImpl.Provider(player));
			if (player instanceof ServerPlayerEntity) {
				attachCapability(evt, InternalTimers.NAME, new InternalTimers.Provider());
				attachCapability(evt, InternalAbilities.NAME, new InternalAbilities.Provider((ServerPlayerEntity) player));
			}
		}
	}

	private static void attachCapability(AttachCapabilitiesEvent<Entity> evt, ResourceLocation name, BasicCapabilityResolver<?> cap) {
		evt.addCapability(name, cap);
		evt.addListener(cap::invalidateAll);
	}

	@SubscribeEvent
	public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		PacketHandler.sendFragmentedEmcPacket(player);

		player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(knowledge -> {
			knowledge.sync(player);
			PlayerHelper.updateScore(player, PlayerHelper.SCOREBOARD_EMC, knowledge.getEmc());
		});

		DTCore.debugLog("Sent knowledge to {}", player.getName());
	}

	@SubscribeEvent
	public static void onConstruct(EntityEvent.EntityConstructing evt) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER // No world to check yet
			&& evt.getEntity() instanceof PlayerEntity && !(evt.getEntity() instanceof FakePlayer)) {
			TransmutationOffline.clear(evt.getEntity().getUniqueID());
			DTCore.debugLog("Clearing offline data cache in preparation to load online data");
		}
	}
}