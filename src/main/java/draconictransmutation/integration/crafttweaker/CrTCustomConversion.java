package draconictransmutation.integration.crafttweaker;

import java.util.Map;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;

import draconictransmutation.api.nss.NormalizedSimpleStack;
import draconictransmutation.integration.crafttweaker.actions.CustomConversionAction;

@ZenRegister
@ZenCodeType.Name("mods.projecte.CustomConversion")
public class CrTCustomConversion {

	@ZenCodeType.Method
	public static void addConversion(NormalizedSimpleStack stack, int amount, Map<NormalizedSimpleStack, Integer> ingredients) {
		CraftTweakerAPI.apply(new CustomConversionAction(stack, amount, ingredients));
	}
}