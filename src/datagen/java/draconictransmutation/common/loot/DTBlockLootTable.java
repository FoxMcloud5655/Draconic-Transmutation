package draconictransmutation.common.loot;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import draconictransmutation.gameObjs.registries.DTBlocks;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.TNTBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.util.IItemProvider;

public class DTBlockLootTable extends BlockLootTables {

	private final Set<Block> knownBlocks = new HashSet<>();

	@Override
	protected void addTables() {
		registerDropSelfLootTable(DTBlocks.TRANSMUTATION_TABLE.getBlock());
	}

	@Override
	public void registerDropping(@Nonnull Block block, @Nonnull IItemProvider drop) {
		//Override to use our own dropping method that names the loot table
		registerLootTable(block, dropping(drop));
	}

	protected static LootTable.Builder dropping(IItemProvider item) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(item, LootPool.builder().rolls(ConstantRange.of(1)).name("main").addEntry(ItemLootEntry.builder(item))));
	}

	private void registerCustomTNT(Block tnt) {
		registerLootTable(tnt, LootTable.builder().addLootPool(withSurvivesExplosion(tnt, LootPool.builder().rolls(ConstantRange.of(1))
				.name("main")
				.addEntry(ItemLootEntry.builder(tnt).acceptCondition(BlockStateProperty.builder(tnt)
						.fromProperties(StatePropertiesPredicate.Builder.newBuilder().withBoolProp(TNTBlock.UNSTABLE, false)))))));
	}

	@Override
	protected void registerLootTable(@Nonnull Block block, @Nonnull LootTable.Builder table) {
		//Overwrite the core register method to add to our list of known blocks
		super.registerLootTable(block, table);
		knownBlocks.add(block);
	}

	@Nonnull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return knownBlocks;
	}
}