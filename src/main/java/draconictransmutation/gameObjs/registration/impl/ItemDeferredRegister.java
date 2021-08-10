package draconictransmutation.gameObjs.registration.impl;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registration.WrappedDeferredRegister;
import draconictransmutation.gameObjs.registries.DTItems;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDeferredRegister extends WrappedDeferredRegister<Item> {

	private static final ItemGroup creativeTab = new ItemGroup(DTCore.MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(DTItems.PHILOSOPHERS_STONE);
		}

		@Nonnull
		@Override
		public ITextComponent getGroupName() {
			//Overwrite the lang key to match the one representing ProjectE
			return DTLang.DRACONICTRANSMUTATION.translate();
		}
	};

	public ItemDeferredRegister() {
		super(ForgeRegistries.ITEMS);
	}

	public static Item.Properties getBaseProperties() {
		return new Item.Properties().group(creativeTab);
	}

	public ItemRegistryObject<Item> register(String name) {
		return register(name, Item::new);
	}

	public ItemRegistryObject<Item> registerFireImmune(String name) {
		return registerFireImmune(name, Item::new);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, UnaryOperator.identity());
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerFireImmune(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, Item.Properties::isImmuneToFire);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStack(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, properties -> properties.maxStackSize(1));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStackFireImmune(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, properties -> properties.maxStackSize(1).isImmuneToFire());
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup, UnaryOperator<Item.Properties> propertyModifier) {
		return register(name, () -> sup.apply(propertyModifier.apply(getBaseProperties())));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Supplier<? extends ITEM> sup) {
		return register(name, sup, ItemRegistryObject::new);
	}
}