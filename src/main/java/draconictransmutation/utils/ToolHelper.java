package draconictransmutation.utils;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import draconictransmutation.api.ProjectEAPI;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ToolHelper {
	/**
	 * Performs a set of actions, until we find a success or run out of actions.
	 *
	 * @implNote Only returns that we failed if all the tested actions failed.
	 */
	@SafeVarargs
	public static ActionResultType performActions(ActionResultType firstAction, Supplier<ActionResultType>... secondaryActions) {
		if (firstAction == ActionResultType.SUCCESS) {
			return ActionResultType.SUCCESS;
		}
		ActionResultType result = firstAction;
		boolean hasFailed = result == ActionResultType.FAIL;
		for (Supplier<ActionResultType> secondaryAction : secondaryActions) {
			result = secondaryAction.get();
			if (result == ActionResultType.SUCCESS) {
				//If we were successful
				return ActionResultType.SUCCESS;
			}
			hasFailed &= result == ActionResultType.FAIL;
		}
		if (hasFailed) {
			//If at least one step failed, consider ourselves unsuccessful
			return ActionResultType.FAIL;
		}
		return ActionResultType.PASS;
	}

	private static int getCharge(ItemStack stack) {
		return stack.getCapability(ProjectEAPI.CHARGE_ITEM_CAPABILITY).map(itemCharge -> itemCharge.getCharge(stack)).orElse(0);
	}

	private static byte getMode(ItemStack stack) {
		return stack.getCapability(ProjectEAPI.MODE_CHANGER_ITEM_CAPABILITY).map(itemMode -> itemMode.getMode(stack)).orElse((byte) 0);
	}

	@FunctionalInterface
	public interface RayTracePointer {

		RayTraceResult rayTrace(World world, PlayerEntity player, FluidMode fluidMode);
	}

	public static class ChargeAttributeCache {

		private static final UUID CHARGE_MODIFIER = UUID.fromString("69ADE509-46FF-3725-92AC-F59FB052BEC7");

		//Note: It is an array map instead of hash map as the number of charges are very small by default
		private final Int2ObjectMap<Multimap<Attribute, AttributeModifier>> cachedMaps = new Int2ObjectArrayMap<>();

		public Multimap<Attribute, AttributeModifier> addChargeAttributeModifier(Multimap<Attribute, AttributeModifier> currentModifiers,
				@Nonnull EquipmentSlotType slot, ItemStack stack) {
			if (slot == EquipmentSlotType.MAINHAND) {
				int charge = getCharge(stack);
				if (charge > 0) {
					return cachedMaps.computeIfAbsent(charge, c -> {
						Builder<Attribute, AttributeModifier> attributesBuilder = ImmutableMultimap.builder();
						attributesBuilder.putAll(currentModifiers);
						//If we have any charge take it into account for calculating the damage
						attributesBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(CHARGE_MODIFIER, "Charge modifier", c, Operation.ADDITION));
						return attributesBuilder.build();
					});
				}
			}
			return currentModifiers;
		}
	}
}