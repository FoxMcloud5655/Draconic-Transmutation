package draconictransmutation.gameObjs.registration.impl;

import draconictransmutation.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;

public class IRecipeSerializerRegistryObject<RECIPE extends IRecipe<?>, SERIALIZER extends IRecipeSerializer<RECIPE>> extends WrappedRegistryObject<SERIALIZER> {

	public IRecipeSerializerRegistryObject(RegistryObject<SERIALIZER> registryObject) {
		super(registryObject);
	}
}