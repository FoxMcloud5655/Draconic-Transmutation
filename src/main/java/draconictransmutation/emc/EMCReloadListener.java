package draconictransmutation.emc;

import javax.annotation.Nonnull;

import draconictransmutation.DTCore;
import draconictransmutation.api.nss.AbstractNSSTag;
import draconictransmutation.config.CustomEMCParser;
import draconictransmutation.network.PacketHandler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

//Note: Has to be IResourceManagerReloadListener, so that it works properly on servers
public class EMCReloadListener implements IResourceManagerReloadListener {

	private final DataPackRegistries dataPackRegistries;

	public EMCReloadListener(DataPackRegistries dataPackRegistries) {
		this.dataPackRegistries = dataPackRegistries;
	}

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		long start = System.currentTimeMillis();

		//Clear the cached created tags
		AbstractNSSTag.clearCreatedTags();
		CustomEMCParser.init();

		try {
			EMCMappingHandler.map(dataPackRegistries, resourceManager);
			DTCore.LOGGER.info("Registered " + EMCMappingHandler.getEmcMapSize() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
			PacketHandler.sendFragmentedEmcPacketToAll();
		} catch (Throwable t) {
			DTCore.LOGGER.error("Error calculating EMC values", t);
		}
	}
}