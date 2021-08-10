package draconictransmutation.utils.text;

import draconictransmutation.DTCore;
import net.minecraft.util.Util;

public enum DTLang implements ILangEntry {
	//Vanilla
	UNKNOWN_TAG("arguments.item.tag.unknown"),
	//Misc
	DRACONICTRANSMUTATION("misc", "mod_name"),
	SECONDS("misc", "seconds"),
	EVERY_TICK("misc", "seconds.every_tick"),
	UPDATE_AVAILABLE("misc", "update.available"),
	UPDATE_GET_IT("misc", "update.get_it"),
	BLACKLIST("misc", "blacklist"),
	WHITELIST("misc", "whitelist"),
	DENSITY_MODE_TARGET("misc", "mode_switch"),
	//Command
	CLEAR_KNOWLEDGE_NOTIFY("command", "clear_knowledge.notify"),
	CLEAR_KNOWLEDGE_SUCCESS("command", "clear_knowledge.success"),
	DUMP_MISSING_EMC_NONE_MISSING("command", "dump_missing_emc.none_missing"),
	DUMP_MISSING_EMC_ONE_MISSING("command", "dump_missing_emc.one_missing"),
	DUMP_MISSING_EMC_MULTIPLE_MISSING("command", "dump_missing_emc.multiple_missing"),
	COMMAND_INVALID_ITEM("command", "emc.invalid_item"),
	COMMAND_NO_ITEM("command", "emc.no_item"),
	COMMAND_REMOVE_SUCCESS("command", "remove.success"),
	COMMAND_RESET_SUCCESS("command", "reset.success"),
	COMMAND_SET_SUCCESS("command", "set.success"),
	RELOAD_NOTICE("command", "reload.notice"),
	//EMC
	EMC("emc", "emc"),
	EMC_MAX_GEN_RATE("emc", "max_gen_rate"),
	EMC_MAX_OUTPUT_RATE("emc", "max_output_rate"),
	EMC_MAX_STORAGE("emc", "max_storage"),
	EMC_STORED("emc", "stored"),
	EMC_HAS_KNOWLEDGE("emc", "has_knowledge"),
	EMC_TOOLTIP("emc", "tooltip"),
	EMC_STACK_TOOLTIP("emc", "tooltip.stack"),
	EMC_TOOLTIP_WITH_SELL("emc", "tooltip.with_sell"),
	EMC_STACK_TOOLTIP_WITH_SELL("emc", "tooltip.stack.with_sell"),
	EMC_TOO_MUCH("emc", "too_much"),
	//JEI
	JEI_COLLECTOR("jei", "collector"),
	WORLD_TRANSMUTE("jei", "world_transmute"),
	WORLD_TRANSMUTE_DESCRIPTION("jei", "world_transmute.description"),
	//Mode
	CURRENT_MODE("mode", "current"),
	INVALID_MODE("mode", "invalid"),
	MODE_SWITCH("mode", "switch"),
	MODE_PHILOSOPHER_1("mode", "philosopher.1"),
	MODE_PHILOSOPHER_2("mode", "philosopher.2"),
	MODE_PHILOSOPHER_3("mode", "philosopher.3"),
	//Tooltips
	TOOLTIP_PHILOSTONE("tooltip", "philostone"),
	TOOLTIP_STORED_XP("tooltip", "stored_xp"),
	//Transmutation GUI
	TRANSMUTATION_TRANSMUTE("transmutation", "transmute"),
	TRANSMUTATION_LEARNED_1("transmutation", "learned.1"),
	TRANSMUTATION_LEARNED_2("transmutation", "learned.2"),
	TRANSMUTATION_LEARNED_3("transmutation", "learned.3"),
	TRANSMUTATION_LEARNED_4("transmutation", "learned.4"),
	TRANSMUTATION_LEARNED_5("transmutation", "learned.5"),
	TRANSMUTATION_LEARNED_6("transmutation", "learned.6"),
	TRANSMUTATION_LEARNED_7("transmutation", "learned.7"),
	TRANSMUTATION_LEARNED_8("transmutation", "learned.8"),
	TRANSMUTATION_UNLEARNED_1("transmutation", "unlearned.1"),
	TRANSMUTATION_UNLEARNED_2("transmutation", "unlearned.2"),
	TRANSMUTATION_UNLEARNED_3("transmutation", "unlearned.3"),
	TRANSMUTATION_UNLEARNED_4("transmutation", "unlearned.4"),
	TRANSMUTATION_UNLEARNED_5("transmutation", "unlearned.5"),
	TRANSMUTATION_UNLEARNED_6("transmutation", "unlearned.6"),
	TRANSMUTATION_UNLEARNED_7("transmutation", "unlearned.7"),
	TRANSMUTATION_UNLEARNED_8("transmutation", "unlearned.8"),
	TRANSMUTATION_UNLEARNED_9("transmutation", "unlearned.9"),
	//Advancements
	ADVANCEMENTS_DT_DESCRIPTION("advancements", "description"),
	ADVANCEMENTS_PHILO_STONE("advancements", "philo_stone"),
	ADVANCEMENTS_PHILO_STONE_DESCRIPTION("advancements", "philo_stone.description"),
	ADVANCEMENTS_TRANSMUTATION_TABLE("advancements", "transmutation_table"),
	ADVANCEMENTS_TRANSMUTATION_TABLE_DESCRIPTION("advancements", "transmutation_table.description"),
	ADVANCEMENTS_TRANSMUTATION_TABLET("advancements", "transmutation_tablet"),
	ADVANCEMENTS_TRANSMUTATION_TABLET_DESCRIPTION("advancements", "transmutation_tablet.description"),
	;

	private final String key;

	DTLang(String type, String path) {
		this(Util.makeTranslationKey(type, DTCore.rl(path)));
	}

	DTLang(String key) {
		this.key = key;
	}

	@Override
	public String getTranslationKey() {
		return key;
	}
}