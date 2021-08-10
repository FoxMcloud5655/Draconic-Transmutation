package draconictransmutation.utils;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import draconictransmutation.api.ItemInfo;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.item.IItemEmcHolder;
import draconictransmutation.api.capabilities.tile.IEmcStorage.EmcAction;
import draconictransmutation.config.DTConfig;
import draconictransmutation.emc.nbt.NBTManager;
import draconictransmutation.utils.text.DTLang;
import draconictransmutation.utils.text.ILangEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Helper class for EMC. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class EMCHelper {

	/**
	 * Consumes EMC from fuel items or Klein Stars Any extra EMC is discarded !!! To retain remainder EMC use ItemPE.consumeFuel()
	 *
	 * @implNote Order it tries to extract from is, Curios, Offhand, main inventory
	 */
	public static long consumePlayerFuel(PlayerEntity player, long minFuel) {
		if (player.abilities.isCreativeMode) {
			return minFuel;
		}
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				long actualExtracted = tryExtract(curios.getStackInSlot(i), minFuel);
				if (actualExtracted > 0) {
					player.openContainer.detectAndSendChanges();
					return actualExtracted;
				}
			}
		}

		ItemStack offhand = player.getHeldItemOffhand();

		if (!offhand.isEmpty()) {
			long actualExtracted = tryExtract(offhand, minFuel);
			if (actualExtracted > 0) {
				player.openContainer.detectAndSendChanges();
				return actualExtracted;
			}
		}

		Optional<IItemHandler> itemHandlerCap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (itemHandlerCap.isPresent()) {
			//Ensure that we have an item handler capability, because if for example the player is dead we will not
			IItemHandler inv = itemHandlerCap.get();
			Map<Integer, Integer> map = new LinkedHashMap<>();
			boolean metRequirement = false;
			long emcConsumed = 0;
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) {
					continue;
				}
				long actualExtracted = tryExtract(stack, minFuel);
				if (actualExtracted > 0) {
					player.openContainer.detectAndSendChanges();
					return actualExtracted;
				}
			}
			if (metRequirement) {
				for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
					inv.extractItem(entry.getKey(), entry.getValue(), false);
				}
				player.openContainer.detectAndSendChanges();
				return emcConsumed;
			}
		}
		return -1;
	}

	private static long tryExtract(@Nonnull ItemStack stack, long minFuel) {
		if (stack.isEmpty()) {
			return 0;
		}
		Optional<IItemEmcHolder> holderCapability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
		if (holderCapability.isPresent()) {
			IItemEmcHolder emcHolder = holderCapability.get();
			long simulatedExtraction = emcHolder.extractEmc(stack, minFuel, EmcAction.SIMULATE);
			if (simulatedExtraction == minFuel) {
				return emcHolder.extractEmc(stack, simulatedExtraction, EmcAction.EXECUTE);
			}
		}
		return 0;
	}

	public static boolean doesItemHaveEmc(ItemInfo info) {
		return getEmcValue(info) > 0;
	}

	public static boolean doesItemHaveEmc(ItemStack stack) {
		return getEmcValue(stack) > 0;
	}

	public static boolean doesItemHaveEmc(IItemProvider item) {
		return getEmcValue(item) > 0;
	}

	public static long getEmcValue(IItemProvider item) {
		return item == null ? 0 : getEmcValue(ItemInfo.fromItem(item.asItem()));
	}

	/**
	 * Does not consider stack size
	 */
	public static long getEmcValue(ItemStack stack) {
		return stack.isEmpty() ? 0 : getEmcValue(ItemInfo.fromStack(stack));
	}

	public static long getEmcValue(ItemInfo info) {
		return NBTManager.getEmcValue(info);
	}

	public static long getEmcSellValue(ItemStack stack) {
		return stack.isEmpty() ? 0 : getEmcSellValue(ItemInfo.fromStack(stack));
	}

	public static long getEmcSellValue(ItemInfo info) {
		return getEmcSellValue(getEmcValue(info));
	}

	public static long getEmcSellValue(long originalValue) {
		if (originalValue <= 0) {
			return 0;
		}
		long emc = (long) Math.floor(originalValue * DTConfig.server.difficulty.covalenceLoss.get());
		if (emc < 1) {
			if (DTConfig.server.difficulty.covalenceLossRounding.get()) {
				emc = 1;
			} else {
				emc = 0;
			}
		}
		return emc;
	}

	public static ITextComponent getEmcTextComponent(long emc, int stackSize) {
		if (DTConfig.server.difficulty.covalenceLoss.get() == 1.0) {
			ILangEntry prefix;
			String value;
			if (stackSize > 1) {
				prefix = DTLang.EMC_STACK_TOOLTIP;
				value = Constants.EMC_FORMATTER.format(BigInteger.valueOf(emc).multiply(BigInteger.valueOf(stackSize)));
			} else {
				prefix = DTLang.EMC_TOOLTIP;
				value = Constants.EMC_FORMATTER.format(emc);
			}
			return prefix.translateColored(TextFormatting.YELLOW, TextFormatting.WHITE, value);
		}
		//Sell enabled
		long emcSellValue = getEmcSellValue(emc);
		ILangEntry prefix;
		String value;
		String sell;
		if (stackSize > 1) {
			prefix = DTLang.EMC_STACK_TOOLTIP_WITH_SELL;
			BigInteger bigIntStack = BigInteger.valueOf(stackSize);
			value = Constants.EMC_FORMATTER.format(BigInteger.valueOf(emc).multiply(bigIntStack));
			sell = Constants.EMC_FORMATTER.format(BigInteger.valueOf(emcSellValue).multiply(bigIntStack));
		} else {
			prefix = DTLang.EMC_TOOLTIP_WITH_SELL;
			value = Constants.EMC_FORMATTER.format(emc);
			sell = Constants.EMC_FORMATTER.format(emcSellValue);
		}
		return prefix.translateColored(TextFormatting.YELLOW, TextFormatting.WHITE, value, TextFormatting.BLUE, sell);
	}

	public static long getEMCPerDurability(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else if (stack.isDamageable()) {
			ItemStack stackCopy = stack.copy();
			stackCopy.setDamage(0);
			long emc = (long) Math.ceil(getEmcValue(stackCopy) / (double) stack.getMaxDamage());
			return Math.max(emc, 1);
		}
		return 1;
	}

	/**
	 * Adds the given amount to the amount of unprocessed EMC the stack has. The amount returned should be used for figuring out how much EMC actually gets removed. While
	 * the remaining fractional EMC will be stored in UnprocessedEMC.
	 *
	 * @param stack  The stack to set the UnprocessedEMC tag to.
	 * @param amount The partial amount of EMC to add with the current UnprocessedEMC
	 *
	 * @return The amount of non fractional EMC no longer being stored in UnprocessedEMC.
	 */
	public static long removeFractionalEMC(ItemStack stack, double amount) {
		CompoundNBT nbt = stack.getOrCreateTag();
		double unprocessedEMC = nbt.getDouble(Constants.NBT_KEY_UNPROCESSED_EMC);
		unprocessedEMC += amount;
		long toRemove = (long) unprocessedEMC;
		unprocessedEMC -= toRemove;
		nbt.putDouble(Constants.NBT_KEY_UNPROCESSED_EMC, unprocessedEMC);
		return toRemove;
	}
}