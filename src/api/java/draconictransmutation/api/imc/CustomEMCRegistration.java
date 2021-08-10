package draconictransmutation.api.imc;

import draconictransmutation.api.nss.NormalizedSimpleStack;

public class CustomEMCRegistration {

	private final NormalizedSimpleStack stack;
	private final long value;

	/**
	 * @param stack Defines the stack to set the EMC for.
	 * @param value The EMC value to register for the given stack.
	 */
	public CustomEMCRegistration(NormalizedSimpleStack stack, long value) {
		this.stack = stack;
		this.value = value;
	}

	public NormalizedSimpleStack getStack() {
		return stack;
	}

	public long getValue() {
		return value;
	}
}
