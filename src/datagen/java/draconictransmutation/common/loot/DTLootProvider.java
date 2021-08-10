package draconictransmutation.common.loot;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;

public class DTLootProvider extends LootTableProvider {

	public DTLootProvider(DataGenerator gen) {
		super(gen);
	}

	@Nonnull
	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
		ImmutableList.Builder<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> builder = new ImmutableList.Builder<>();
		builder.add(Pair.of(DTBlockLootTable::new, LootParameterSets.BLOCK));
		return builder.build();
	}

	@Override
	protected void validate(@Nonnull Map<ResourceLocation, LootTable> map, @Nonnull ValidationTracker validationtracker) {
		//NO-OP, as we don't
	}
}