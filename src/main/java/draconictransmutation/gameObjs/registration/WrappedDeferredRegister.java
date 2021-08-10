package draconictransmutation.gameObjs.registration;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class WrappedDeferredRegister<T extends IForgeRegistryEntry<T>> {

	@Nonnull
	protected final DeferredRegister<T> internal;

	protected WrappedDeferredRegister(IForgeRegistry<T> registry) {
		internal = DeferredRegister.create(registry, DTCore.MODID);
	}

	protected <I extends T, W extends WrappedRegistryObject<I>> W register(String name, Supplier<? extends I> sup, Function<RegistryObject<I>, W> objectWrapper) {
		return objectWrapper.apply(internal.register(name, sup));
	}

	public void register(IEventBus bus) {
		internal.register(bus);
	}
}