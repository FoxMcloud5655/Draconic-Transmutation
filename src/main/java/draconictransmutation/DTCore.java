package draconictransmutation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.item.IExtraFunction;
import draconictransmutation.api.capabilities.item.IItemCharge;
import draconictransmutation.api.capabilities.item.IItemEmcHolder;
import draconictransmutation.api.capabilities.item.IModeChanger;
import draconictransmutation.api.capabilities.tile.IEmcStorage;
import draconictransmutation.config.DTConfig;
import draconictransmutation.config.DTModConfig;
import draconictransmutation.emc.EMCMappingHandler;
import draconictransmutation.emc.EMCReloadListener;
import draconictransmutation.emc.json.NSSSerializer;
import draconictransmutation.emc.mappers.recipe.CraftingMapper;
import draconictransmutation.emc.nbt.NBTManager;
import draconictransmutation.gameObjs.registries.DTBlocks;
import draconictransmutation.gameObjs.registries.DTContainerTypes;
import draconictransmutation.gameObjs.registries.DTItems;
import draconictransmutation.gameObjs.registries.DTRecipeSerializers;
import draconictransmutation.gameObjs.registries.DTSoundEvents;
import draconictransmutation.handlers.InternalAbilities;
import draconictransmutation.handlers.InternalTimers;
import draconictransmutation.impl.IMCHandler;
import draconictransmutation.impl.TransmutationOffline;
import draconictransmutation.impl.capability.ChargeItemDefaultImpl;
import draconictransmutation.impl.capability.EmcHolderItemDefaultImpl;
import draconictransmutation.impl.capability.EmcStorageDefaultImpl;
import draconictransmutation.impl.capability.ExtraFunctionItemDefaultImpl;
import draconictransmutation.impl.capability.KnowledgeImpl;
import draconictransmutation.impl.capability.ModeChangerItemDefaultImpl;
import draconictransmutation.integration.IntegrationHelper;
import draconictransmutation.network.PacketHandler;
import draconictransmutation.network.ThreadCheckUpdate;
import draconictransmutation.network.commands.ClearKnowledgeCMD;
import draconictransmutation.network.commands.DumpMissingEmc;
import draconictransmutation.network.commands.RemoveEmcCMD;
import draconictransmutation.network.commands.ResetEmcCMD;
import draconictransmutation.network.commands.SetEmcCMD;
import draconictransmutation.network.commands.argument.ColorArgument;
import draconictransmutation.network.commands.argument.NSSItemArgument;
import draconictransmutation.utils.DummyIStorage;
import draconictransmutation.utils.WorldTransmutations;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(DTCore.MODID)
@Mod.EventBusSubscriber(modid = DTCore.MODID)
public class DTCore {

	public static final String MODID = ProjectEAPI.DRACONICTRANSMUTATION_MODID;
	public static final String MODNAME = "Draconic Transmutation";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(null, "[" + MODNAME + "]");
	public static boolean DEV_ENVIRONMENT;
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static ModContainer MOD_CONTAINER;

	public static void debugLog(String msg, Object... args) {
		if (DEV_ENVIRONMENT || DTConfig.common.debugLogging.get()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}

	public static ResourceLocation rl(String path) {
		return new ResourceLocation(MODID, path);
	}

	public DTCore() {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::imcQueue);
		modEventBus.addListener(this::imcHandle);
		modEventBus.addListener(this::onConfigLoad);
		DTBlocks.BLOCKS.register(modEventBus);
		DTContainerTypes.CONTAINER_TYPES.register(modEventBus);
		DTItems.ITEMS.register(modEventBus);
		DTRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		DTSoundEvents.SOUND_EVENTS.register(modEventBus);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::addReloadListenersLowest);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		MinecraftForge.EVENT_BUS.addListener(this::serverQuit);

		//Register our config files
		DTConfig.register();
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		DEV_ENVIRONMENT = FMLLoader.getNameFunction("srg").isPresent();

		KnowledgeImpl.init();
		CapabilityManager.INSTANCE.register(InternalTimers.class, new DummyIStorage<>(), InternalTimers::new);
		CapabilityManager.INSTANCE.register(InternalAbilities.class, new DummyIStorage<>(), () -> new InternalAbilities(null));
		CapabilityManager.INSTANCE.register(IExtraFunction.class, new DummyIStorage<>(), ExtraFunctionItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IItemCharge.class, new DummyIStorage<>(), ChargeItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IItemEmcHolder.class, new DummyIStorage<>(), EmcHolderItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IModeChanger.class, new DummyIStorage<>(), ModeChangerItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IEmcStorage.class, new DummyIStorage<>(), EmcStorageDefaultImpl::new);

		new ThreadCheckUpdate().start();

		EMCMappingHandler.loadMappers();
		CraftingMapper.loadMappers();
		NBTManager.loadProcessors();

		event.enqueueWork(() -> {
			PacketHandler.register();
			// internals unsafe
			ArgumentTypes.register(MODID + ":color", ColorArgument.class, new ArgumentSerializer<>(ColorArgument::new));
			ArgumentTypes.register(MODID + ":nss", NSSItemArgument.class, new ArgumentSerializer<>(NSSItemArgument::new));
		});
	}

	private void imcQueue(InterModEnqueueEvent event) {
		WorldTransmutations.init();
		NSSSerializer.init();
		IntegrationHelper.sendIMCMessages(event);
	}

	private void imcHandle(InterModProcessEvent event) {
		IMCHandler.handleMessages();
	}

	private void onConfigLoad(ModConfig.ModConfigEvent configEvent) {
		//Note: We listen to both the initial load and the reload, so as to make sure that we fix any accidentally
		// cached values from calls before the initial loading
		ModConfig config = configEvent.getConfig();
		//Make sure it is for the same modid as us
		if (config.getModId().equals(MODID) && config instanceof DTModConfig) {
			((DTModConfig) config).clearCache();
		}
	}

	private void addReloadListenersLowest(AddReloadListenerEvent event) {
		//Note: We register our listener for this event on lowest priority so that if other mods register custom NSSTags
		// or other things that need to be sync'd/reloaded they have a chance to go before we do
		event.addListener(new EMCReloadListener(event.getDataPackRegistries()));
	}

	private void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSource> root = Commands.literal("draconictransmutation")
				.then(ClearKnowledgeCMD.register())
				.then(DumpMissingEmc.register())
				.then(RemoveEmcCMD.register())
				.then(ResetEmcCMD.register())
				.then(SetEmcCMD.register());
		event.getDispatcher().register(root);
	}

	private void serverQuit(FMLServerStoppedEvent event) {
		TransmutationOffline.cleanAll();
		EMCMappingHandler.clearEmcMap();
	}
}