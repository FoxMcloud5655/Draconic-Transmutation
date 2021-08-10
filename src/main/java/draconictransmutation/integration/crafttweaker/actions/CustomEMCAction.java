package draconictransmutation.integration.crafttweaker.actions;

import javax.annotation.Nonnull;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;

import draconictransmutation.api.nss.NormalizedSimpleStack;
import draconictransmutation.integration.crafttweaker.mappers.CrTCustomEMCMapper;
import draconictransmutation.utils.Constants;

public class CustomEMCAction implements IUndoableAction {

	@Nonnull
	private final NormalizedSimpleStack stack;
	private final long emc;

	public CustomEMCAction(@Nonnull NormalizedSimpleStack stack, long emc) {
		this.stack = stack;
		this.emc = emc;
	}

	@Override
	public void apply() {
		CrTCustomEMCMapper.registerCustomEMC(stack, emc);
	}

	@Override
	public String describe() {
		return "Registered emc value of '" + Constants.EMC_FORMATTER.format(emc) + "' for: " + stack;
	}

	@Override
	public void undo() {
		CrTCustomEMCMapper.unregisterNSS(stack);
	}

	@Override
	public String describeUndo() {
		return "Undoing emc registration for: " + stack;
	}
}