package draconictransmutation.config;

import java.nio.file.Path;

import draconictransmutation.DTCore;
import net.minecraftforge.fml.loading.FMLPaths;

public class DTConfig {

	public static final Path CONFIG_DIR;
	public static final ServerConfig server = new ServerConfig();
	public static final CommonConfig common = new CommonConfig();
	public static final ClientConfig client = new ClientConfig();

	static {
		CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(DTCore.MODNAME), DTCore.MODNAME);
	}

	public static void register() {
		registerConfig(server);
		registerConfig(common);
		registerConfig(client);
	}

	/**
	 * Creates a mod config so that {@link net.minecraftforge.fml.config.ConfigTracker} will track it and sync server configs from server to client.
	 */
	public static void registerConfig(IDTConfig config) {
		DTModConfig peModConfig = new DTModConfig(DTCore.MOD_CONTAINER, config);
		if (config.addToContainer()) {
			DTCore.MOD_CONTAINER.addConfig(peModConfig);
		}
	}
}