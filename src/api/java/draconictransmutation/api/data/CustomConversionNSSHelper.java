package draconictransmutation.api.data;

import javax.annotation.ParametersAreNonnullByDefault;

import draconictransmutation.api.nss.NSSFake;
import draconictransmutation.api.nss.NSSFluid;
import draconictransmutation.api.nss.NSSItem;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.FluidStack;

/**
 * Helper interface to hold some helper wrapper methods to make it cleaner to interact with various built in types of {@link NormalizedSimpleStack}s.
 *
 * @param <BUILDER> Type of the {@link ConversionBuilder} that will be returned by the conversion methods.
 *
 * @implNote The reason this is an interface is to reduce the duplicate code between the various implementers and also keep the files cleaner to read.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
interface CustomConversionNSSHelper<BUILDER extends ConversionBuilder<BUILDER>> {

	/**
	 * Creates a {@link BUILDER} that outputs the given amount of the given {@link NormalizedSimpleStack}.
	 *
	 * @param output Stack produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	BUILDER conversion(NormalizedSimpleStack output, int amount);

	/**
	 * Creates a {@link BUILDER} that outputs one of the given {@link NormalizedSimpleStack}.
	 *
	 * @param output Stack produced by the conversion.
	 */
	default BUILDER conversion(NormalizedSimpleStack output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap an {@link ItemStack} into a {@link NormalizedSimpleStack} and output amount, and then creates a {@link BUILDER} representing it.
	 *
	 * @param output Stack produced by the conversion.
	 *
	 * @apiNote Either this method or {@link #conversion(NormalizedSimpleStack, int)} using {@link NSSItem#createItem(IItemProvider, net.minecraft.nbt.CompoundNBT)}
	 * should be used if NBT specifics are needed.
	 */
	default BUILDER conversion(ItemStack output) {
		return conversion(NSSItem.createItem(output), output.getCount());
	}

	/**
	 * Helper method to wrap an {@link IItemProvider} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Item produced by the conversion.
	 */
	default BUILDER conversion(IItemProvider output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap an {@link IItemProvider} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Item produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(IItemProvider output, int amount) {
		return conversion(NSSItem.createItem(output), amount);
	}

	/**
	 * Helper method to wrap an {@link ITag<Item>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Item tag produced by the conversion.
	 */
	default BUILDER conversion(ITag<Item> output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap an {@link ITag<Item>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Item tag produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(ITag<Item> output, int amount) {
		return conversion(NSSItem.createTag(output), amount);
	}

	/**
	 * Helper method to wrap a {@link FluidStack} into a {@link NormalizedSimpleStack} and output amount, and then creates a {@link BUILDER} representing it.
	 *
	 * @param output Stack produced by the conversion.
	 *
	 * @apiNote Either this method or {@link #conversion(NormalizedSimpleStack, int)} using {@link NSSFluid#createFluid(Fluid, net.minecraft.nbt.CompoundNBT)} should be
	 * used if NBT specifics are needed.
	 */
	default BUILDER conversion(FluidStack output) {
		return conversion(NSSFluid.createFluid(output), output.getAmount());
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Fluid produced by the conversion.
	 */
	default BUILDER conversion(Fluid output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Fluid produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(Fluid output, int amount) {
		return conversion(NSSFluid.createFluid(output), amount);
	}

	/**
	 * Helper method to wrap an {@link ITag<Fluid>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Fluid tag produced by the conversion.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default BUILDER conversionFluid(ITag<Fluid> output) {
		return conversionFluid(output, 1);
	}

	/**
	 * Helper method to wrap an {@link ITag<Fluid>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Fluid tag produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default BUILDER conversionFluid(ITag<Fluid> output, int amount) {
		return conversion(NSSFluid.createTag(output), amount);
	}

	/**
	 * Helper method to wrap a "fake" {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param fake Description of the "fake" {@link NormalizedSimpleStack}.
	 */
	default BUILDER conversion(String fake) {
		return conversion(fake, 1);
	}

	/**
	 * Helper method to wrap a "fake" {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param fake   Description of the "fake" {@link NormalizedSimpleStack}.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(String fake, int amount) {
		return conversion(NSSFake.create(fake), amount);
	}
}