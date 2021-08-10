package draconictransmutation.integration.crafttweaker.nss;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;

import draconictransmutation.api.nss.NormalizedSimpleStack;

@ZenRegister
@NativeTypeRegistration(value = NormalizedSimpleStack.class, zenCodeName = "mods.projecte.NormalizedSimpleStack")
public class CrTExpandNormalizedSimpleStack {

	@ZenCodeType.Caster
	public static String asString(NormalizedSimpleStack internal) {
		return internal.toString();
	}
}