package draconictransmutation.gameObjs.registration;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DoubleDeferredRegister<PRIMARY extends IForgeRegistryEntry<PRIMARY>, SECONDARY extends IForgeRegistryEntry<SECONDARY>> {

	@Nonnull
	protected final DeferredRegister<PRIMARY> primaryRegister;
	@Nonnull
	protected final DeferredRegister<SECONDARY> secondaryRegister;

	public DoubleDeferredRegister(IForgeRegistry<PRIMARY> primaryRegistry, IForgeRegistry<SECONDARY> secondaryRegistry) {
		primaryRegister = DeferredRegister.create(primaryRegistry, DTCore.MODID);
		secondaryRegister = DeferredRegister.create(secondaryRegistry, DTCore.MODID);
	}

	public <P extends PRIMARY, S extends SECONDARY, W extends DoubleWrappedRegistryObject<P, S>> W register(String name, Supplier<? extends P> primarySupplier,
			Supplier<? extends S> secondarySupplier, BiFunction<RegistryObject<P>, RegistryObject<S>, W> objectWrapper) {
		return objectWrapper.apply(primaryRegister.register(name, primarySupplier), secondaryRegister.register(name, secondarySupplier));
	}

	public <P extends PRIMARY, S extends SECONDARY, W extends DoubleWrappedRegistryObject<P, S>> W register(String name, Supplier<? extends P> primarySupplier,
			Function<P, S> secondarySupplier, BiFunction<RegistryObject<P>, RegistryObject<S>, W> objectWrapper) {
		RegistryObject<P> primaryObject = primaryRegister.register(name, primarySupplier);
		return objectWrapper.apply(primaryObject, secondaryRegister.register(name, () -> secondarySupplier.apply(primaryObject.get())));
	}

	public void register(IEventBus bus) {
		primaryRegister.register(bus);
		secondaryRegister.register(bus);
	}
}