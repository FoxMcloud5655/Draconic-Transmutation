package draconictransmutation.utils;

import java.util.Locale;

import draconictransmutation.DTCore;
import draconictransmutation.utils.text.IHasTranslationKey;
import net.minecraft.util.Util;

public enum DTKeybind implements IHasTranslationKey {
	CHARGE,
	EXTRA_FUNCTION,
	MODE;

	private final String translationKey;

	DTKeybind() {
		this.translationKey = Util.makeTranslationKey("key", DTCore.rl(name().toLowerCase(Locale.ROOT)));
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}