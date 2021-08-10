package draconictransmutation.gameObjs.registries;

import draconictransmutation.gameObjs.container.TransmutationContainer;
import draconictransmutation.gameObjs.registration.impl.ContainerTypeDeferredRegister;
import draconictransmutation.gameObjs.registration.impl.ContainerTypeRegistryObject;

public class DTContainerTypes {

	public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister();
	public static final ContainerTypeRegistryObject<TransmutationContainer> TRANSMUTATION_CONTAINER = CONTAINER_TYPES.register(DTBlocks.TRANSMUTATION_TABLE, TransmutationContainer::fromNetwork);
}