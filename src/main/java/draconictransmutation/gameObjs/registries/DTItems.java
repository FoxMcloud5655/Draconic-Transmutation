package draconictransmutation.gameObjs.registries;

import draconictransmutation.gameObjs.items.PhilosophersStone;
import draconictransmutation.gameObjs.items.TransmutationTablet;
import draconictransmutation.gameObjs.registration.impl.ItemDeferredRegister;
import draconictransmutation.gameObjs.registration.impl.ItemRegistryObject;

public class DTItems {
	public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister();
	public static final ItemRegistryObject<PhilosophersStone> PHILOSOPHERS_STONE = ITEMS.registerNoStack("philosophers_stone", PhilosophersStone::new);	public static final ItemRegistryObject<TransmutationTablet> TRANSMUTATION_TABLET = ITEMS.registerNoStackFireImmune("transmutation_tablet", TransmutationTablet::new);
}