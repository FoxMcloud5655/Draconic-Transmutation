package draconictransmutation.integration.crafttweaker.actions;

import javax.annotation.Nullable;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.blamejared.crafttweaker.impl_native.blocks.ExpandBlockState;

import draconictransmutation.utils.WorldTransmutations;
import net.minecraft.block.BlockState;

public abstract class WorldTransmuteAction implements IUndoableAction {

	protected final BlockState input;
	protected final BlockState output;
	@Nullable
	protected final BlockState sneakOutput;

	private WorldTransmuteAction(BlockState input, BlockState output, @Nullable BlockState sneakOutput) {
		this.input = input;
		this.output = output;
		this.sneakOutput = sneakOutput;
	}

	protected void apply(boolean add) {
		if (add) {
			WorldTransmutations.register(this.input, this.output, this.sneakOutput);
		} else {
			WorldTransmutations.getWorldTransmutations().removeIf(entry -> entry.getOrigin() == this.input &&
																		   entry.getResult() == this.output && entry.getAltResult() == this.sneakOutput);
		}
	}

	public static class Add extends WorldTransmuteAction {

		public Add(BlockState input, BlockState output, @Nullable BlockState sneakOutput) {
			super(input, output, sneakOutput);
		}

		@Override
		public void apply() {
			apply(true);
		}

		@Override
		public String describe() {
			if (sneakOutput == null) {
				return "Adding world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
					   ExpandBlockState.getCommandString(output);
			}
			return "Adding world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
				   ExpandBlockState.getCommandString(output) + " and secondary output: " + ExpandBlockState.getCommandString(sneakOutput);
		}

		@Override
		public void undo() {
			apply(false);
		}

		@Override
		public String describeUndo() {
			if (sneakOutput == null) {
				return "Undoing addition of world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
					   ExpandBlockState.getCommandString(output);
			}
			return "Undoing addition of world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
				   ExpandBlockState.getCommandString(output) + " and secondary output: " + ExpandBlockState.getCommandString(sneakOutput);
		}
	}

	public static class Remove extends WorldTransmuteAction {

		public Remove(BlockState input, BlockState output, @Nullable BlockState sneakOutput) {
			super(input, output, sneakOutput);
		}

		@Override
		public void apply() {
			apply(false);
		}

		@Override
		public String describe() {
			if (sneakOutput == null) {
				return "Removing world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
					   ExpandBlockState.getCommandString(output);
			}
			return "Removing world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
				   ExpandBlockState.getCommandString(output) + " and secondary output: " + ExpandBlockState.getCommandString(sneakOutput);
		}

		@Override
		public void undo() {
			apply(true);
		}

		@Override
		public String describeUndo() {
			if (sneakOutput == null) {
				return "Undoing removal of world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
					   ExpandBlockState.getCommandString(output);
			}
			return "Undoing removal of world transmutation recipe for: " + ExpandBlockState.getCommandString(input) + " with output: " +
				   ExpandBlockState.getCommandString(output) + " and secondary output: " + ExpandBlockState.getCommandString(sneakOutput);
		}
	}

	public static class RemoveAll implements IUndoableAction {

		@Override
		public void apply() {
			WorldTransmutations.getWorldTransmutations().clear();
		}

		@Override
		public String describe() {
			return "Removing all world transmutation recipes";
		}

		@Override
		public void undo() {
			WorldTransmutations.resetWorldTransmutations();
		}

		@Override
		public String describeUndo() {
			return "Restored world transmutation recipes to default";
		}
	}
}