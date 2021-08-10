package draconictransmutation.handlers;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import draconictransmutation.capability.managing.BasicCapabilityResolver;
import draconictransmutation.utils.PlayerHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

public final class InternalAbilities {

	@CapabilityInject(InternalAbilities.class)
	public static Capability<InternalAbilities> CAPABILITY = null;
	public static final ResourceLocation NAME = DTCore.rl("internal_abilities");
	private final ServerPlayerEntity player;

	public InternalAbilities(ServerPlayerEntity player) {
		this.player = player;
	}

	private boolean checkArmorHotbarCurios(Predicate<ItemStack> checker) {
		for (ItemStack stack : player.inventory.armorInventory) {
			if (checker.test(stack)) {
				return true;
			}
		}
		return checkHotbarCurios(checker);
	}

	private boolean checkHotbarCurios(Predicate<ItemStack> checker) {
		for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
			if (checker.test(player.inventory.getStackInSlot(i))) {
				return true;
			}
		}
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				if (checker.test(curios.getStackInSlot(i))) {
					return true;
				}
			}
		}
		return false;
	}

	public static class Provider extends BasicCapabilityResolver<InternalAbilities> {

		public Provider(ServerPlayerEntity player) {
			super(() -> new InternalAbilities(player));
		}

		@Nonnull
		@Override
		public Capability<InternalAbilities> getMatchingCapability() {
			return CAPABILITY;
		}
	}
}