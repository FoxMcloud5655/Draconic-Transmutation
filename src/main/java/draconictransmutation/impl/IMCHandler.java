package draconictransmutation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import draconictransmutation.DTCore;
import draconictransmutation.api.imc.CustomEMCRegistration;
import draconictransmutation.api.imc.IMCMethods;
import draconictransmutation.api.imc.NSSCreatorInfo;
import draconictransmutation.api.imc.WorldTransmutationEntry;
import draconictransmutation.api.nss.NSSCreator;
import draconictransmutation.emc.json.NSSSerializer;
import draconictransmutation.emc.mappers.APICustomEMCMapper;
import draconictransmutation.utils.WorldTransmutations;
import net.minecraftforge.fml.InterModComms;

public class IMCHandler {

	public static void handleMessages() {
		List<WorldTransmutationEntry> entries = new ArrayList<>();
		InterModComms.getMessages(DTCore.MODID, IMCMethods.REGISTER_WORLD_TRANSMUTATION::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof WorldTransmutationEntry)
				.forEach(msg -> {
					WorldTransmutationEntry transmutationEntry = (WorldTransmutationEntry) msg.getMessageSupplier().get();
					entries.add(transmutationEntry);
					if (transmutationEntry.getAltResult() == null) {
						DTCore.debugLog("Mod: '{}' registered World Transmutation from: '{}', to: '{}'", msg.getSenderModId(),
								transmutationEntry.getOrigin(), transmutationEntry.getResult());
					} else {
						DTCore.debugLog("Mod: '{}' registered World Transmutation from: '{}', to: '{}', with sneak output of: '{}'", msg.getSenderModId(),
								transmutationEntry.getOrigin(), transmutationEntry.getResult(), transmutationEntry.getAltResult());
					}
				});
		WorldTransmutations.setWorldTransmutation(entries);

		InterModComms.getMessages(DTCore.MODID, IMCMethods.REGISTER_CUSTOM_EMC::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof CustomEMCRegistration)
				.forEach(msg -> APICustomEMCMapper.INSTANCE.registerCustomEMC(msg.getSenderModId(), (CustomEMCRegistration) msg.getMessageSupplier().get()));

		//Note: It is first come first serve. If we already received a value for it we don't try to overwrite it, but we do log a warning
		Map<String, NSSCreator> creators = new HashMap<>();
		InterModComms.getMessages(DTCore.MODID, IMCMethods.REGISTER_NSS_SERIALIZER::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof NSSCreatorInfo)
				.forEach(msg -> {
					NSSCreatorInfo creatorInfo = (NSSCreatorInfo) msg.getMessageSupplier().get();
					String key = creatorInfo.getKey();
					if (creators.containsKey(key)) {
						DTCore.LOGGER.warn("Mod: '{}' tried to register NSS creator with key: '{}', but another mod already registered that key.", msg.getSenderModId(), key);
					} else {
						creators.put(key, creatorInfo.getCreator());
						DTCore.debugLog("Mod: '{}' registered NSS creator with key: '{}'", msg.getSenderModId(), key);
					}
				});
		NSSSerializer.INSTANCE.setCreators(creators);
	}
}