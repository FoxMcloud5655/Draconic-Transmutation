package draconictransmutation.gameObjs.registries;

import draconictransmutation.gameObjs.customRecipes.PhiloStoneSmeltingRecipe;
import draconictransmutation.gameObjs.registration.impl.IRecipeSerializerDeferredRegister;
import draconictransmutation.gameObjs.registration.impl.IRecipeSerializerRegistryObject;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

public class DTRecipeSerializers {
	public static final IRecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new IRecipeSerializerDeferredRegister();
	public static final IRecipeSerializerRegistryObject<PhiloStoneSmeltingRecipe, SpecialRecipeSerializer<PhiloStoneSmeltingRecipe>> PHILO_STONE_SMELTING = RECIPE_SERIALIZERS.register("philo_stone_smelting", () -> new SpecialRecipeSerializer<>(PhiloStoneSmeltingRecipe::new));
}