package draconictransmutation.gameObjs.registration;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ParametersAreNonnullByDefault
public class WrappedRegistryObject<T extends IForgeRegistryEntry<? super T>> implements Supplier<T>, INamedEntry {

	@Nonnull
	protected final RegistryObject<T> registryObject;

	protected WrappedRegistryObject(RegistryObject<T> registryObject) {
		this.registryObject = registryObject;
	}

	@Nonnull
	@Override
	public T get() {
		return registryObject.get();
	}

	@Override
	public String getInternalRegistryName() {
		return registryObject.getId().getPath();
	}
}