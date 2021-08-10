package draconictransmutation.common.recipe;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registries.DTBlocks;
import draconictransmutation.gameObjs.registries.DTItems;
import draconictransmutation.gameObjs.registries.DTRecipeSerializers;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

public class DTRecipeProvider extends RecipeProvider {

	public DTRecipeProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		addCustomRecipeSerializer(consumer, DTRecipeSerializers.PHILO_STONE_SMELTING.get());
		addTransmutationTableRecipes(consumer);
		addMiscToolRecipes(consumer);
	}

	private static void addCustomRecipeSerializer(Consumer<IFinishedRecipe> consumer, SpecialRecipeSerializer<?> serializer) {
		CustomRecipeBuilder.customRecipe(serializer).build(consumer, serializer.getRegistryName().toString());
	}

	private static void addMiscToolRecipes(Consumer<IFinishedRecipe> consumer) {
		//Philosopher's Stone
		philosopherStoneRecipe(consumer, false);
		philosopherStoneRecipe(consumer, true);
	}

	private static void philosopherStoneRecipe(Consumer<IFinishedRecipe> consumer, boolean alternate) {
		String name = DTItems.PHILOSOPHERS_STONE.get().getRegistryName().toString();
		ShapedRecipeBuilder philoStone = ShapedRecipeBuilder.shapedRecipe(DTItems.PHILOSOPHERS_STONE)
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.key('G', Tags.Items.DUSTS_GLOWSTONE)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE))
				.setGroup(name);
		if (alternate) {
			philoStone.patternLine("GRG")
					.patternLine("RDR")
					.patternLine("GRG")
					.build(consumer, name + "_alt");
		} else {
			philoStone.patternLine("RGR")
					.patternLine("GDG")
					.patternLine("RGR")
					.build(consumer);
		}
	}

	private static void addTransmutationTableRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(DTBlocks.TRANSMUTATION_TABLE)
				.patternLine("OSO")
				.patternLine("SPS")
				.patternLine("OSO")
				.key('S', Tags.Items.STONE)
				.key('O', Tags.Items.OBSIDIAN)
				.key('P', DTItems.PHILOSOPHERS_STONE)
				.addCriterion("has_philo_stone", hasItem(DTItems.PHILOSOPHERS_STONE))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DTItems.TRANSMUTATION_TABLET)
				.patternLine("DSD")
				.patternLine("STS")
				.patternLine("DSD")
				.key('S', Tags.Items.STONE)
				.key('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.key('T', DTBlocks.TRANSMUTATION_TABLE)
				.addCriterion("has_table", hasItem(DTBlocks.TRANSMUTATION_TABLE))
				.build(consumer);
	}

	private static void philoConversionRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider a, int aAmount, IItemProvider b, int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		ShapelessRecipeBuilder.shapelessRecipe(b, bAmount)
				.addIngredient(DTItems.PHILOSOPHERS_STONE)
				.addIngredient(a, aAmount)
				.addCriterion("has_" + aName, hasItems(DTItems.PHILOSOPHERS_STONE, a))
				.build(consumer, DTCore.rl("conversions/" + aName + "_to_" + bName));
		ShapelessRecipeBuilder.shapelessRecipe(a, aAmount)
				.addIngredient(DTItems.PHILOSOPHERS_STONE)
				.addIngredient(b, bAmount)
				.addCriterion("has_" + bName, hasItems(DTItems.PHILOSOPHERS_STONE, b))
				.build(consumer, DTCore.rl("conversions/" + bName + "_to_" + aName));
	}

	private static void philoConversionRecipe(Consumer<IFinishedRecipe> consumer, ITag<Item> aTag, IItemProvider a, int aAmount, ITag<Item> bTag, IItemProvider b,
			int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		//A to B
		philoConversionRecipe(consumer, aName, aTag, aAmount, bName, b, bAmount);
		//B to A
		philoConversionRecipe(consumer, bName, bTag, bAmount, aName, a, aAmount);
	}

	private static void philoConversionRecipe(Consumer<IFinishedRecipe> consumer, String inputName, ITag<Item> inputTag, int inputAmount, String outputName,
			IItemProvider output, int outputAmount) {
		ShapelessRecipeBuilder bToA = ShapelessRecipeBuilder.shapelessRecipe(output, outputAmount)
				.addIngredient(DTItems.PHILOSOPHERS_STONE)
				.addCriterion("has_" + inputName, hasItems(DTItems.PHILOSOPHERS_STONE, inputTag));
		for (int i = 0; i < inputAmount; i++) {
			bToA.addIngredient(inputTag);
		}
		bToA.build(consumer, DTCore.rl("conversions/" + inputName + "_to_" + outputName));
	}

	private static String getName(IItemProvider item) {
		return item.asItem().getRegistryName().getPath();
	}

	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider... items) {
		return InventoryChangeTrigger.Instance.forItems(items);
	}

	@SafeVarargs
	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider item, ITag<Item>... tags) {
		return hasItems(new IItemProvider[]{item}, tags);
	}

	@SafeVarargs
	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider[] items, ITag<Item>... tags) {
		ItemPredicate[] predicates = new ItemPredicate[items.length + tags.length];
		for (int i = 0; i < items.length; ++i) {
			predicates[i] = ItemPredicate.Builder.create().item(items[i]).build();
		}
		for (int i = 0; i < tags.length; ++i) {
			predicates[items.length + i] = ItemPredicate.Builder.create().tag(tags[i]).build();
		}
		return hasItem(predicates);
	}
}