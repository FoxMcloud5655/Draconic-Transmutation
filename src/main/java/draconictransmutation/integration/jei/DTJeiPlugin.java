package draconictransmutation.integration.jei;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.container.PhilosStoneContainer;
import draconictransmutation.gameObjs.registries.DTItems;
import draconictransmutation.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class DTJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = DTCore.rl("main");

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new WorldTransmuteRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(PhilosStoneContainer.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(DTItems.PHILOSOPHERS_STONE), VanillaRecipeCategoryUid.CRAFTING, WorldTransmuteRecipeCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		registry.addRecipes(WorldTransmuteRecipeCategory.getAllTransmutations(), WorldTransmuteRecipeCategory.UID);
	}
}