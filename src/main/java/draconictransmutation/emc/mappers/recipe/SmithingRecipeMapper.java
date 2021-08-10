package draconictransmutation.emc.mappers.recipe;

import java.util.Arrays;
import java.util.Collection;

import draconictransmutation.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;

@RecipeTypeMapper
public class SmithingRecipeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return "Smithing";
	}

	@Override
	public String getDescription() {
		return "Maps smithing recipes.";
	}

	@Override
	public boolean canHandle(IRecipeType<?> recipeType) {
		return recipeType == IRecipeType.SMITHING;
	}

	@Override
	protected Collection<Ingredient> getIngredients(IRecipe<?> recipe) {
		SmithingRecipe smithingRecipe = (SmithingRecipe) recipe;
		//Smithing recipes don't implement getIngredient with the inputs so we need to fake it
		return Arrays.asList(smithingRecipe.base, smithingRecipe.addition);
	}
}