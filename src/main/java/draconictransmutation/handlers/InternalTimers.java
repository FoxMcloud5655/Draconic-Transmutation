package draconictransmutation.handlers;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import draconictransmutation.capability.managing.BasicCapabilityResolver;
import draconictransmutation.config.DTConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class InternalTimers {

	@CapabilityInject(InternalTimers.class)
	public static Capability<InternalTimers> CAPABILITY = null;
	public static final ResourceLocation NAME = DTCore.rl("internal_timers");

	private final Timer repair = new Timer();
	private final Timer heal = new Timer();
	private final Timer feed = new Timer();

	public void tick() {
		repair.tick();
		heal.tick();
		feed.tick();
	}

	public void activateRepair() {
		repair.shouldUpdate = DTConfig.server.cooldown.player.repair.get() != -1;
	}

	public void activateHeal() {
		heal.shouldUpdate = DTConfig.server.cooldown.player.heal.get() != -1;
	}

	public void activateFeed() {
		feed.shouldUpdate = DTConfig.server.cooldown.player.feed.get() != -1;
	}

	public boolean canRepair() {
		if (repair.tickCount == 0) {
			repair.tickCount = DTConfig.server.cooldown.player.repair.get();
			repair.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public boolean canHeal() {
		if (heal.tickCount == 0) {
			heal.tickCount = DTConfig.server.cooldown.player.heal.get();
			heal.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public boolean canFeed() {
		if (feed.tickCount == 0) {
			feed.tickCount = DTConfig.server.cooldown.player.feed.get();
			feed.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public static class Provider extends BasicCapabilityResolver<InternalTimers> {

		public Provider() {
			super(InternalTimers::new);
		}

		@Nonnull
		@Override
		public Capability<InternalTimers> getMatchingCapability() {
			return CAPABILITY;
		}
	}

	private static class Timer {

		private int tickCount = 0;
		private boolean shouldUpdate = false;

		private void tick() {
			if (shouldUpdate) {
				if (tickCount > 0) {
					//Ensure we don't go negative if we are set to go off every tick
					tickCount--;
				}
				shouldUpdate = false;
			}
		}
	}
}