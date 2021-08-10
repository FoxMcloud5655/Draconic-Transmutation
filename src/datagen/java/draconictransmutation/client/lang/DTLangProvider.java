package draconictransmutation.client.lang;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registries.DTBlocks;
import draconictransmutation.gameObjs.registries.DTItems;
import draconictransmutation.gameObjs.registries.DTSoundEvents;
import draconictransmutation.utils.DTKeybind;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Util;

public class DTLangProvider extends BaseLanguageProvider {

	public DTLangProvider(DataGenerator gen) {
		super(gen, DTCore.MODID);
	}

	@Override
	protected void addTranslations() {
		addAdvancements();
		addBlocks();
		addCommands();
		addEMC();
		addItems();
		addModes();
		addSubtitles();
		addTooltips();
		addTransmutation();
		//Misc stuff
		add(DTLang.DRACONICTRANSMUTATION, "ProjectE");
		add(DTLang.SECONDS, "%s seconds");
		add(DTLang.EVERY_TICK, "%s seconds (every tick)");
		add(DTLang.UPDATE_AVAILABLE, "New ProjectE update available! Version: %s");
		add(DTLang.UPDATE_GET_IT, "Get it here!");
		add(DTLang.BLACKLIST, "Blacklist");
		add(DTLang.WHITELIST, "Whitelist");
		add(DTLang.DENSITY_MODE_TARGET, "Set target to: %s");
		//Keybinds
		add(DTKeybind.CHARGE, "Charge");
		add(DTKeybind.EXTRA_FUNCTION, "Extra Function");
		add(DTKeybind.MODE, "Change Mode");
		//JEI
		add(DTLang.JEI_COLLECTOR, "Collector Fuel Upgrades");
		add(DTLang.WORLD_TRANSMUTE, "World Transmutation");
		add(DTLang.WORLD_TRANSMUTE_DESCRIPTION, "Click in world, shift click for second output");
	}

	private void addAdvancements() {
		add(DTLang.ADVANCEMENTS_DT_DESCRIPTION, "Correspondent Commerce?");
		add(DTLang.ADVANCEMENTS_PHILO_STONE, "An alchemist's best friend!");
		add(DTLang.ADVANCEMENTS_PHILO_STONE_DESCRIPTION, "Let's get things started! Craft a philosopher's stone");
		add(DTLang.ADVANCEMENTS_TRANSMUTATION_TABLE, "Transmute this into that!");
		add(DTLang.ADVANCEMENTS_TRANSMUTATION_TABLE_DESCRIPTION, "The beginning (and end) of everything.");
		add(DTLang.ADVANCEMENTS_TRANSMUTATION_TABLET, "Transmutation on the go!");
		add(DTLang.ADVANCEMENTS_TRANSMUTATION_TABLET_DESCRIPTION, "And then you thought things couldn't get better.");
	}

	private void addBlocks() {
		add(DTBlocks.TRANSMUTATION_TABLE, "Transmutation Table");
	}

	private void addCommands() {
		add(DTLang.CLEAR_KNOWLEDGE_NOTIFY, "Your transmutation knowledge was cleared by %s!");
		add(DTLang.CLEAR_KNOWLEDGE_SUCCESS, "Cleared knowledge for: %s");
		add(DTLang.DUMP_MISSING_EMC_NONE_MISSING, "All Items have an EMC value.");
		add(DTLang.DUMP_MISSING_EMC_ONE_MISSING, "One Item is missing an EMC value, printing to server log.");
		add(DTLang.DUMP_MISSING_EMC_MULTIPLE_MISSING, "%s Items are missing an EMC value, printing to server log.");
		add(DTLang.COMMAND_INVALID_ITEM, "Error: The item or tag \"%s\" was not found!");
		add(DTLang.COMMAND_NO_ITEM, "Please give an item or tag to change");
		add(DTLang.COMMAND_REMOVE_SUCCESS, "Removed EMC value for %s.");
		add(DTLang.COMMAND_RESET_SUCCESS, "Reset EMC value for %s.");
		add(DTLang.COMMAND_SET_SUCCESS, "Set EMC value for %s to %s!");
		add(DTLang.RELOAD_NOTICE, "Restart or use \"/reload\" when all changes are complete.");
	}

	private void addEMC() {
		add(DTLang.EMC, "%s EMC");
		add(DTLang.EMC_MAX_GEN_RATE, "Maximum Generation Rate: %s EMC/s");
		add(DTLang.EMC_MAX_OUTPUT_RATE, "Maximum Output Rate: %s EMC/s");
		add(DTLang.EMC_MAX_STORAGE, "Maximum Storage: %s EMC");
		add(DTLang.EMC_STORED, "Stored EMC: %s");
		add(DTLang.EMC_HAS_KNOWLEDGE, "Transmutable");
		add(DTLang.EMC_TOOLTIP, "EMC: %s");
		add(DTLang.EMC_STACK_TOOLTIP, "Stack EMC: %s");
		add(DTLang.EMC_TOOLTIP_WITH_SELL, "EMC: %s (%s)");
		add(DTLang.EMC_STACK_TOOLTIP_WITH_SELL, "Stack EMC: %s (%s)");
		add(DTLang.EMC_TOO_MUCH, "WAY TOO MUCH");
		//Postfix names for large EMC values. Names gotten from: https://en.wikipedia.org/wiki/Names_of_large_numbers
		addPostfix(0, "Trillion");
		addPostfix(1, "Quadrillion");
		addPostfix(2, "Quintillion");
		addPostfix(3, "Sextillion");
		addPostfix(4, "Septillion");
		addPostfix(5, "Octillion");
		addPostfix(6, "Nonillion");
		addPostfix(7, "Decillion");
		addPostfix(8, "Undecillion");
		addPostfix(9, "Duodecillion");
		addPostfix(10, "Tredecillion");
		addPostfix(11, "Quattuordecillion");
		addPostfix(12, "Quindecillion");
		addPostfix(13, "Sexdecillion");
		addPostfix(14, "Septendecillion");
		addPostfix(15, "Octodecillion");
		addPostfix(16, "Novemdecillion");
		addPostfix(17, "Vigintillion");
	}

	private void addPostfix(int index, String postfix) {
		add(Util.makeTranslationKey("emc", DTCore.rl("postfix." + index)), "%s " + postfix);
	}

	private void addItems() {
		add(DTItems.PHILOSOPHERS_STONE, "Philosopher's Stone");
		add(DTItems.TRANSMUTATION_TABLET, "Transmutation Tablet");
	}

	private void addModes() {
		add(DTLang.CURRENT_MODE, "Mode: %s");
		add(DTLang.INVALID_MODE, "Invalid Mode");
		add(DTLang.MODE_SWITCH, "Switched to %s Mode");
		add(DTLang.MODE_PHILOSOPHER_1, "Cube");
		add(DTLang.MODE_PHILOSOPHER_2, "Panel");
		add(DTLang.MODE_PHILOSOPHER_3, "Line");
	}

	private void addSubtitles() {
		//TODO: Improve on these if someone has better ideas for the subtitles
		add(DTSoundEvents.WIND_MAGIC, "Wind Magic");
		add(DTSoundEvents.WATER_MAGIC, "Water Magic");
		add(DTSoundEvents.POWER, "Device Powered");
		add(DTSoundEvents.HEAL, "Healing Performed");
		add(DTSoundEvents.DESTRUCT, "Destruction");
		add(DTSoundEvents.CHARGE, "Device Charged");
		add(DTSoundEvents.UNCHARGE, "Device Uncharged");
		add(DTSoundEvents.TRANSMUTE, "Block Transmuted");
	}

	private void addTooltips() {
		add(DTLang.TOOLTIP_PHILOSTONE, "Press %s to open a crafting grid");
		add(DTLang.TOOLTIP_STORED_XP, "Stored XP: %s");
	}

	private void addTransmutation() {
		add(DTLang.TRANSMUTATION_TRANSMUTE, "Transmutation");
		add(DTLang.TRANSMUTATION_LEARNED_1, "L");
		add(DTLang.TRANSMUTATION_LEARNED_2, "e");
		add(DTLang.TRANSMUTATION_LEARNED_3, "a");
		add(DTLang.TRANSMUTATION_LEARNED_4, "r");
		add(DTLang.TRANSMUTATION_LEARNED_5, "n");
		add(DTLang.TRANSMUTATION_LEARNED_6, "e");
		add(DTLang.TRANSMUTATION_LEARNED_7, "d");
		add(DTLang.TRANSMUTATION_LEARNED_8, "!");
		add(DTLang.TRANSMUTATION_UNLEARNED_1, "U");
		add(DTLang.TRANSMUTATION_UNLEARNED_2, "n");
		add(DTLang.TRANSMUTATION_UNLEARNED_3, "l");
		add(DTLang.TRANSMUTATION_UNLEARNED_4, "e");
		add(DTLang.TRANSMUTATION_UNLEARNED_5, "a");
		add(DTLang.TRANSMUTATION_UNLEARNED_6, "r");
		add(DTLang.TRANSMUTATION_UNLEARNED_7, "n");
		add(DTLang.TRANSMUTATION_UNLEARNED_8, "e");
		add(DTLang.TRANSMUTATION_UNLEARNED_9, "d");
	}
}