package draconictransmutation;

import draconictransmutation.gameObjs.gui.GUITransmutation;
import draconictransmutation.gameObjs.registration.impl.ContainerTypeRegistryObject;
import draconictransmutation.gameObjs.registries.DTContainerTypes;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DTCore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

	public static final ResourceLocation ACTIVE_OVERRIDE = DTCore.rl("active");
	public static final ResourceLocation MODE_OVERRIDE = DTCore.rl("mode");

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		registerScreen(DTContainerTypes.TRANSMUTATION_CONTAINER, GUITransmutation::new);
	}

	private static void addPropertyOverrides(ResourceLocation override, IItemPropertyGetter propertyGetter, IItemProvider... itemProviders) {
		for (IItemProvider itemProvider : itemProviders) {
			ItemModelsProperties.registerProperty(itemProvider.asItem(), override, propertyGetter);
		}
	}

	private static <C extends Container, U extends Screen & IHasContainer<C>> void registerScreen(ContainerTypeRegistryObject<C> type, IScreenFactory<C, U> factory) {
		ScreenManager.registerFactory(type.get(), factory);
	}
}