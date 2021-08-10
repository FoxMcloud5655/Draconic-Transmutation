package draconictransmutation.emc.mappers;

import java.util.HashMap;
import java.util.Map;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import draconictransmutation.DTCore;
import draconictransmutation.api.mapper.EMCMapper;
import draconictransmutation.api.mapper.IEMCMapper;
import draconictransmutation.api.mapper.collector.IMappingCollector;
import draconictransmutation.api.nss.NSSItem;
import draconictransmutation.api.nss.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.registries.ForgeRegistries;

@EMCMapper
public class TippedArrowMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		int recipeCount = 0;
		NSSItem nssArrow = NSSItem.createItem(Items.ARROW);
		for (Potion potionType : ForgeRegistries.POTION_TYPES.getValues()) {
			Map<NormalizedSimpleStack, Integer> ingredientsWithAmount = new HashMap<>();
			ingredientsWithAmount.put(nssArrow, 8);
			ingredientsWithAmount.put(NSSItem.createItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), potionType)), 1);
			mapper.addConversion(8, NSSItem.createItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.TIPPED_ARROW), potionType)), ingredientsWithAmount);
			recipeCount++;
		}
		DTCore.debugLog("TippedArrowMapper Statistics:");
		DTCore.debugLog("Found {} Tipped Arrow Recipes", recipeCount);
	}

	@Override
	public String getName() {
		return "TippedArrowMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for all lingering potions to arrow recipes";
	}
}