package draconictransmutation.client;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registration.impl.BlockRegistryObject;
import draconictransmutation.gameObjs.registries.DTBlocks;
import draconictransmutation.gameObjs.registries.DTItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DTItemModelProvider extends ItemModelProvider {

	public DTItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, DTCore.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		blockParentModel(DTBlocks.TRANSMUTATION_TABLE);
		registerGenerated(DTItems.PHILOSOPHERS_STONE, DTItems.TRANSMUTATION_TABLET);
	}

	private void blockParentModel(BlockRegistryObject<?, ?>... blocks) {
		for (BlockRegistryObject<?, ?> block : blocks) {
			String name = getName(block);
			withExistingParent(name, modLoc("block/" + name));
		}
	}

	protected ResourceLocation itemTexture(IItemProvider itemProvider) {
		return modLoc("item/" + getName(itemProvider));
	}

	protected void registerGenerated(IItemProvider... itemProviders) {
		for (IItemProvider itemProvider : itemProviders) {
			generated(itemProvider);
		}
	}

	protected ItemModelBuilder generated(IItemProvider itemProvider) {
		return generated(itemProvider, itemTexture(itemProvider));
	}

	protected ItemModelBuilder generated(IItemProvider itemProvider, ResourceLocation texture) {
		return generated(getName(itemProvider), texture);
	}

	protected ItemModelBuilder generated(String name, ResourceLocation texture) {
		return withExistingParent(name, "item/generated").texture("layer0", texture);
	}

	private static String getName(IItemProvider itemProvider) {
		return itemProvider.asItem().getRegistryName().getPath();
	}
}