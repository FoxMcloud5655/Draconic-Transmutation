package draconictransmutation.integration.jei.world_transmute;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import draconictransmutation.api.imc.WorldTransmutationEntry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class WorldTransmuteEntry {

	private final ItemStack inputItem;
	private final ItemStack leftOutputItem;
	private final ItemStack rightOutputItem;
	private final FluidStack inputFluid;
	private final FluidStack leftOutputFluid;
	private final FluidStack rightOutputFluid;

	public WorldTransmuteEntry(WorldTransmutationEntry transmutationEntry) {
		Block inputBlock = transmutationEntry.getOrigin().getBlock();
		BlockState leftOutput = transmutationEntry.getResult();
		BlockState rightOutput = transmutationEntry.getAltResult();

		inputFluid = fluidFromBlock(inputBlock);
		if (inputFluid == null) {
			inputItem = itemFromBlock(inputBlock, transmutationEntry.getOrigin());
		} else {
			inputItem = ItemStack.EMPTY;
		}
		if (leftOutput != null) {
			leftOutputFluid = fluidFromBlock(leftOutput.getBlock());
			if (leftOutputFluid == null) {
				leftOutputItem = itemFromBlock(leftOutput.getBlock(), leftOutput);
			} else {
				leftOutputItem = ItemStack.EMPTY;
			}
		} else {
			leftOutputItem = ItemStack.EMPTY;
			leftOutputFluid = FluidStack.EMPTY;
		}
		if (rightOutput != null) {
			rightOutputFluid = fluidFromBlock(rightOutput.getBlock());
			if (rightOutputFluid == null) {
				rightOutputItem = itemFromBlock(rightOutput.getBlock(), rightOutput);
			} else {
				rightOutputItem = ItemStack.EMPTY;
			}
		} else {
			rightOutputItem = ItemStack.EMPTY;
			rightOutputFluid = FluidStack.EMPTY;
		}
	}

	private FluidStack fluidFromBlock(Block block) {
		if (block instanceof FlowingFluidBlock) {
			return new FluidStack(((FlowingFluidBlock) block).getFluid(), FluidAttributes.BUCKET_VOLUME);
		} else if (block instanceof IFluidBlock) {
			return new FluidStack(((IFluidBlock) block).getFluid(), FluidAttributes.BUCKET_VOLUME);
		}
		return null;
	}

	private ItemStack itemFromBlock(Block block, BlockState state) {
		try {
			//We don't have a world or position, but try pick block anyways
			return block.getPickBlock(state, null, null, null, null);
		} catch (Exception e) {
			//It failed, probably because of the null world and pos
			return new ItemStack(block);
		}
	}

	public boolean isRenderable() {
		boolean hasInput = inputFluid != null || !inputItem.isEmpty();
		boolean hasLeftOutput = leftOutputFluid != null || !leftOutputItem.isEmpty();
		boolean hasRightOutput = rightOutputFluid != null || !rightOutputItem.isEmpty();
		return hasInput && (hasLeftOutput || hasRightOutput);
	}

	public void setIngredients(@Nonnull IIngredients ingredients) {
		if (inputFluid != null) {
			ingredients.setInput(VanillaTypes.FLUID, inputFluid);
		} else if (!inputItem.isEmpty()) {
			ingredients.setInput(VanillaTypes.ITEM, inputItem);
		}

		List<FluidStack> fluidOutputs = new ArrayList<>();
		if (leftOutputFluid != null) {
			fluidOutputs.add(leftOutputFluid);
		}
		if (rightOutputFluid != null) {
			fluidOutputs.add(rightOutputFluid);
		}
		if (!fluidOutputs.isEmpty()) {
			ingredients.setOutputs(VanillaTypes.FLUID, fluidOutputs);
		}

		List<ItemStack> outputList = new ArrayList<>();
		if (!leftOutputItem.isEmpty()) {
			outputList.add(leftOutputItem);
		}
		if (!rightOutputItem.isEmpty()) {
			outputList.add(rightOutputItem);
		}
		if (!outputList.isEmpty()) {
			ingredients.setOutputs(VanillaTypes.ITEM, outputList);
		}
	}

	public ItemStack getInputItem() {
		return inputItem;
	}

	public FluidStack getInputFluid() {
		return inputFluid;
	}
}