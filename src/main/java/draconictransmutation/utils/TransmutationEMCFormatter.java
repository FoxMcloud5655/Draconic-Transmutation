package draconictransmutation.utils;

import draconictransmutation.DTCore;
import draconictransmutation.utils.text.DTLang;
import draconictransmutation.utils.text.TextComponentUtil;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;

public class TransmutationEMCFormatter {

	private static final int MAX_POSTFIX_INDEX = 17;

	public static ITextComponent formatEMC(Number emc) {
		String emcAsString = emc.toString();
		int length = emcAsString.length();
		int splits = (length - 1) / 3;
		if (splits < 4) {
			return TextComponentUtil.getString(Constants.EMC_FORMATTER.format(emc));
		}
		int postfixIndex = splits - 4;
		if (postfixIndex > MAX_POSTFIX_INDEX) {
			//If we have have a number larger than the highest postfix we have a translation key for
			// then display the too much translation key instead. If someone somehow gets to this
			// already absurdly high number and wants it "fixed" then all they would need to do
			// is add more entries to the lang file, and increase the MAX_POSTFIX_INDEX to match
			return DTLang.EMC_TOO_MUCH.translate();
		}
		//Otherwise we need to manually format it
		int extraDigits = length % 3;
		double value;
		if (extraDigits == 0) {
			value = Double.parseDouble(emcAsString.substring(0, 3) + "." + emcAsString.substring(3, 5));
		} else if (extraDigits == 1) {
			value = Double.parseDouble(emcAsString.charAt(0) + "." + emcAsString.substring(1, 3));
		} else {//if (extraDigits == 2)
			value = Double.parseDouble(emcAsString.substring(0, 2) + "." + emcAsString.substring(2, 4));
		}
		return TextComponentUtil.smartTranslate(Util.makeTranslationKey("emc", DTCore.rl("postfix." + postfixIndex)), Constants.EMC_FORMATTER.format(value));
	}
}