package draconictransmutation.integration.crafttweaker.actions;

import java.util.Map;
import java.util.Map.Entry;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;

import draconictransmutation.api.nss.NormalizedSimpleStack;
import draconictransmutation.integration.crafttweaker.mappers.CrTConversionEMCMapper;
import draconictransmutation.integration.crafttweaker.mappers.CrTConversionEMCMapper.CrTConversion;

public class CustomConversionAction implements IUndoableAction {

	private final CrTConversion conversion;

	public CustomConversionAction(NormalizedSimpleStack output, int amount, Map<NormalizedSimpleStack, Integer> ingredients) {
		conversion = new CrTConversion(output, amount, ingredients);
	}

	@Override
	public void apply() {
		CrTConversionEMCMapper.addConversion(conversion);
	}

	@Override
	public String describe() {
		StringBuilder inputString = new StringBuilder();
		for (Entry<NormalizedSimpleStack, Integer> entry : conversion.ingredients.entrySet()) {
			if (inputString.length() > 0) {
				//If we already have elements, prepend a comma
				inputString.append(", ");
			}
			int amount = entry.getValue();
			if (amount > 1) {
				inputString.append(amount).append(" ");
			}
			inputString.append(entry.getKey());
		}
		return "Added custom conversion creating '" + conversion.amount + "' of " + conversion.output + ", from: " + inputString;
	}

	@Override
	public void undo() {
		CrTConversionEMCMapper.removeConversion(conversion);
	}

	@Override
	public String describeUndo() {
		return "Undoing adding of custom conversion creating '" + conversion.amount + "' of " + conversion.output;
	}
}