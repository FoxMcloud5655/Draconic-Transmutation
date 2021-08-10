package draconictransmutation.emc.mappers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import draconictransmutation.DTCore;
import draconictransmutation.api.ItemInfo;
import draconictransmutation.api.mapper.EMCMapper;
import draconictransmutation.api.mapper.IEMCMapper;
import draconictransmutation.api.mapper.collector.IMappingCollector;
import draconictransmutation.api.nss.NSSFluid;
import draconictransmutation.api.nss.NSSItem;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import draconictransmutation.utils.ItemInfoHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.registries.ForgeRegistries;

@EMCMapper
public class BrewingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	//Note: We don't bother keeping track of the output field as we query the recipe to see if it is valid
	private static final Set<ItemInfo> allReagents = new HashSet<>();
	private static final Set<ItemInfo> allInputs = new HashSet<>();
	private static int totalConversions;
	private static int totalPotionItems;

	private static List<Object> itemConversions;
	private static List<Object> typeConversions;
	private static Class<?> mixPredicateClass;

	private static boolean mapAllReagents() {
		if (itemConversions == null || typeConversions == null) {
			//Get references to the lists of Mixing Predicates if either of our references is null
			try {
				typeConversions = ObfuscationReflectionHelper.getPrivateValue(PotionBrewing.class, null, "field_185213_a");//POTION_TYPE_CONVERSIONS
				if (typeConversions == null) {
					DTCore.LOGGER.error("Error getting type conversion field.");
					return false;
				}
				itemConversions = ObfuscationReflectionHelper.getPrivateValue(PotionBrewing.class, null, "field_185214_b");//POTION_ITEM_CONVERSIONS
			} catch (UnableToFindFieldException | UnableToAccessFieldException e) {
				DTCore.LOGGER.error("Error getting conversion field: ", e);
				return false;
			}
			if (itemConversions == null) {
				DTCore.LOGGER.error("Error getting item conversion field.");
				return false;
			}
		}
		int conversionCount = itemConversions.size() + typeConversions.size();
		//Use our best guess of if our cache is invalid because someone added to the list at runtime
		// Shouldn't happen but there is nothing by default making it so that the list because immutable
		// But if it does happen clear our cached values and then try to get everything again
		if (totalConversions == conversionCount) {
			return true;
		}
		allReagents.clear();

		if (mixPredicateClass == null) {
			//If we don't have a reference to the MixPredicate class try to get it
			try {
				mixPredicateClass = Class.forName("net.minecraft.potion.PotionBrewing$MixPredicate");
			} catch (ClassNotFoundException e) {
				DTCore.LOGGER.error("Brewing mapper: could not find MixPredicate");
				return false;
			}
		}
		if (addReagents(itemConversions) || addReagents(typeConversions)) {
			return false;
		}
		totalConversions = conversionCount;
		return true;
	}

	private static boolean addReagents(List<Object> conversions) {
		for (Object conversion : conversions) {
			try {
				Ingredient reagent = ObfuscationReflectionHelper.getPrivateValue((Class<Object>) mixPredicateClass, conversion, "field_185199_b");
				if (reagent == null) {
					DTCore.LOGGER.error("Brewing mapper: could not find reagents field.");
					return true;
				}
				for (ItemStack r : reagent.getMatchingStacks()) {
					allReagents.add(ItemInfo.fromStack(r));
				}
			} catch (Exception ex) {
				DTCore.LOGGER.error("Brewing mapper: could not find field: {}", ex.getMessage());
				return true;
			}
		}
		return false;
	}

	private static void mapAllInputs() {
		//Use our best guess of if our cache is invalid because someone added to the list at runtime
		// Shouldn't happen but there is nothing by default making it so that the list because immutable
		// But if it does happen clear our cached values and then try to get everything again
		int count = PotionBrewing.POTION_ITEMS.size();
		if (totalPotionItems == count) {
			return;
		}
		allInputs.clear();
		Set<ItemInfo> inputs = new HashSet<>();
		for (Ingredient potionItem : PotionBrewing.POTION_ITEMS) {
			ItemStack[] matchingStacks = getMatchingStacks(potionItem);
			if (matchingStacks != null) {
				//Silently ignore any invalid potion items (ingredients that may be tags) this should never be the case
				// unless someone ATs the map and inserts a custom ingredient into it, but just in case, don't crash
				for (ItemStack input : matchingStacks) {
					inputs.add(ItemInfo.fromStack(input));
				}
			}
		}
		for (Potion potion : ForgeRegistries.POTION_TYPES.getValues()) {
			for (ItemInfo input : inputs) {
				allInputs.add(ItemInfoHelper.makeWithPotion(input, potion));
			}
		}
		totalPotionItems = count;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		boolean vanillaRetrieved = mapAllReagents();
		if (vanillaRetrieved) {
			mapAllInputs();
		}

		//Add conversion for empty bottle + water to water bottle
		Map<NormalizedSimpleStack, Integer> waterIngredients = new HashMap<>();
		waterIngredients.put(NSSItem.createItem(Items.GLASS_BOTTLE), 1);
		waterIngredients.put(NSSFluid.createTag(FluidTags.WATER), FluidAttributes.BUCKET_VOLUME / 3);
		mapper.addConversion(1, NSSItem.createItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER)), waterIngredients);

		Set<Class<?>> canNotMap = new HashSet<>();
		int recipeCount = 0;
		List<IBrewingRecipe> recipes = BrewingRecipeRegistry.getRecipes();
		for (IBrewingRecipe recipe : recipes) {
			if (recipe instanceof BrewingRecipe) {
				BrewingRecipe brewingRecipe = (BrewingRecipe) recipe;
				Ingredient input = brewingRecipe.getInput();
				Ingredient reagent = brewingRecipe.getIngredient();
				ItemStack output = brewingRecipe.getOutput();
				ItemStack[] validInputs = getMatchingStacks(input);
				ItemStack[] validReagents = getMatchingStacks(reagent);
				if (validInputs == null || validReagents == null) {
					//Skip brewing recipes that we are not able to process such as ones using tags
					// as ingredients, as tags don't exist when the brewing recipe is being defined
					continue;
				}
				NormalizedSimpleStack nssOut = NSSItem.createItem(output);
				for (ItemStack validInput : validInputs) {
					NormalizedSimpleStack nssInput = NSSItem.createItem(validInput);
					for (ItemStack validReagent : validReagents) {
						Map<NormalizedSimpleStack, Integer> ingredientsWithAmount = new HashMap<>();
						ingredientsWithAmount.put(nssInput, 3);
						ingredientsWithAmount.put(NSSItem.createItem(validReagent), validReagent.getCount());
						//Add the conversion, 3 input + x reagent = 3 y output as strictly speaking the only one of the three parts
						// in the recipe that are required to be one in stack size is the input
						mapper.addConversion(3 * output.getCount(), nssOut, ingredientsWithAmount);
						recipeCount++;
					}
				}
			} else if (recipe instanceof VanillaBrewingRecipe) {
				if (!vanillaRetrieved) {
					//Skip doing vanilla recipes because we failed to get them properly
					canNotMap.add(recipe.getClass());
					continue;
				}
				//Check all known valid inputs and reagents to see which ones create valid inputs
				// Note: Getting the list of outputs while getting reagents will cause things to be missed
				// As the PotionBrewing class does not contain all valid mappings (For example: Potion of luck + gunpowder -> splash potion of luck)
				for (ItemInfo inputInfo : allInputs) {
					ItemStack validInput = inputInfo.createStack();
					NormalizedSimpleStack nssInput = NSSItem.createItem(validInput);
					for (ItemInfo reagentInfo : allReagents) {
						ItemStack validReagent = reagentInfo.createStack();
						ItemStack output = recipe.getOutput(validInput, validReagent);
						if (!output.isEmpty()) {
							Map<NormalizedSimpleStack, Integer> ingredientsWithAmount = new HashMap<>();
							ingredientsWithAmount.put(nssInput, 3);
							ingredientsWithAmount.put(NSSItem.createItem(validReagent), 1);
							//Add the conversion, 3 input + reagent = 3 y output as the output technically could be stacked
							mapper.addConversion(3 * output.getCount(), NSSItem.createItem(output), ingredientsWithAmount);
							recipeCount++;
						}
					}
				}
			} else {
				canNotMap.add(recipe.getClass());
			}
		}

		DTCore.debugLog("BrewingMapper Statistics:");
		DTCore.debugLog("Found {} Brewing Recipes", recipeCount);
		for (Class<?> c : canNotMap) {
			DTCore.debugLog("Could not map Brewing Recipes with Type: {}", c.getName());
		}
	}

	@Override
	public String getName() {
		return "BrewingMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for Brewing Recipes";
	}

	@Nullable
	private static ItemStack[] getMatchingStacks(Ingredient ingredient) {
		try {
			return ingredient.getMatchingStacks();
		} catch (Exception e) {
			return null;
		}
	}
}