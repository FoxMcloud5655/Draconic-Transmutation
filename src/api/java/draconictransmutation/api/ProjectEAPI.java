package draconictransmutation.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import draconictransmutation.api.capabilities.IAlchBagProvider;
import draconictransmutation.api.capabilities.IKnowledgeProvider;
import draconictransmutation.api.capabilities.item.IAlchBagItem;
import draconictransmutation.api.capabilities.item.IAlchChestItem;
import draconictransmutation.api.capabilities.item.IExtraFunction;
import draconictransmutation.api.capabilities.item.IItemCharge;
import draconictransmutation.api.capabilities.item.IItemEmcHolder;
import draconictransmutation.api.capabilities.item.IModeChanger;
import draconictransmutation.api.capabilities.item.IPedestalItem;
import draconictransmutation.api.capabilities.item.IProjectileShooter;
import draconictransmutation.api.capabilities.tile.IEmcStorage;
import draconictransmutation.api.proxy.IEMCProxy;
import draconictransmutation.api.proxy.ITransmutationProxy;

public final class ProjectEAPI {

	private static IEMCProxy emcProxy;
	private static ITransmutationProxy transProxy;
	private static final Logger LOGGER = LogManager.getLogger("draconictransmutationapi");
	public static final String DRACONICTRANSMUTATION_MODID = "draconictransmutation";

	private ProjectEAPI() {
	}

	/**
	 * The capability object for IEmcStorage
	 */
	@CapabilityInject(IEmcStorage.class)
	public static Capability<IEmcStorage> EMC_STORAGE_CAPABILITY = null;

	/**
	 * The capability object for IAlchBagProvider
	 */
	@CapabilityInject(IAlchBagProvider.class)
	public static Capability<IAlchBagProvider> ALCH_BAG_CAPABILITY = null;

	/**
	 * The capability object for IKnowledgeProvider
	 */
	@CapabilityInject(IKnowledgeProvider.class)
	public static Capability<IKnowledgeProvider> KNOWLEDGE_CAPABILITY = null;

	/**
	 * The capability object for IAlchBagItem
	 */
	@CapabilityInject(IAlchBagItem.class)
	public static Capability<IAlchBagItem> ALCH_BAG_ITEM_CAPABILITY = null;

	/**
	 * The capability object for IAlchChestItem
	 */
	@CapabilityInject(IAlchChestItem.class)
	public static Capability<IAlchChestItem> ALCH_CHEST_ITEM_CAPABILITY = null;

	/**
	 * The capability object for IExtraFunction
	 */
	@CapabilityInject(IExtraFunction.class)
	public static Capability<IExtraFunction> EXTRA_FUNCTION_ITEM_CAPABILITY = null;

	/**
	 * The capability object for IItemCharge
	 */
	@CapabilityInject(IItemCharge.class)
	public static Capability<IItemCharge> CHARGE_ITEM_CAPABILITY = null;

	/**
	 * The capability object for IItemEmcHolder
	 */
	@CapabilityInject(IItemEmcHolder.class)
	public static Capability<IItemEmcHolder> EMC_HOLDER_ITEM_CAPABILITY = null;

	/**
	 * The capability object for IModeChanger
	 */
	@CapabilityInject(IModeChanger.class)
	public static Capability<IModeChanger> MODE_CHANGER_ITEM_CAPABILITY = null;

	/**
	 * The capability object for IPedestalItem
	 */
	@CapabilityInject(IPedestalItem.class)
	public static Capability<IPedestalItem> PEDESTAL_ITEM_CAPABILITY = null;

	/**
	 * The capability object for IProjectileShooter
	 */
	@CapabilityInject(IProjectileShooter.class)
	public static Capability<IProjectileShooter> PROJECTILE_SHOOTER_ITEM_CAPABILITY = null;

	/**
	 * Retrieves the proxy for EMC-based API queries.
	 *
	 * @return The proxy for EMC-based API queries
	 */
	public static IEMCProxy getEMCProxy() {
		// Harmless race
		if (emcProxy == null) {
			try {
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.EMCProxyImpl");
				emcProxy = (IEMCProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex) {
				LOGGER.warn("Error retrieving EMCProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return emcProxy;
	}

	/**
	 * Retrieves the proxy for Transmutation-based API queries.
	 *
	 * @return The proxy for Transmutation-based API queries
	 */
	public static ITransmutationProxy getTransmutationProxy() {
		// Harmless race
		if (transProxy == null) {
			try {
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.TransmutationProxyImpl");
				transProxy = (ITransmutationProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex) {
				LOGGER.warn("Error retrieving TransmutationProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return transProxy;
	}
}