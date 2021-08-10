package draconictransmutation.gameObjs.registration.impl;

import draconictransmutation.gameObjs.registration.INamedEntry;
import draconictransmutation.gameObjs.registration.WrappedDeferredRegister;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerTypeDeferredRegister extends WrappedDeferredRegister<ContainerType<?>> {

	public ContainerTypeDeferredRegister() {
		super(ForgeRegistries.CONTAINERS);
	}

	public <CONTAINER extends Container> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider, IContainerFactory<CONTAINER> factory) {
		return register(nameProvider.getInternalRegistryName(), factory);
	}

	public <CONTAINER extends Container> ContainerTypeRegistryObject<CONTAINER> register(String name, IContainerFactory<CONTAINER> factory) {
		return register(name, () -> IForgeContainerType.create(factory), ContainerTypeRegistryObject::new);
	}
}