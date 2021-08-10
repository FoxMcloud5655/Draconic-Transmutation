package draconictransmutation.gameObjs.registration.impl;

import javax.annotation.Nonnull;

import draconictransmutation.gameObjs.registration.WrappedRegistryObject;
import draconictransmutation.utils.text.IHasTranslationKey;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

public class ItemRegistryObject<ITEM extends Item> extends WrappedRegistryObject<ITEM> implements IItemProvider, IHasTranslationKey {

	public ItemRegistryObject(RegistryObject<ITEM> registryObject) {
		super(registryObject);
	}

	@Nonnull
	@Override
	public ITEM asItem() {
		return get();
	}

	@Override
	public String getTranslationKey() {
		return get().getTranslationKey();
	}
}