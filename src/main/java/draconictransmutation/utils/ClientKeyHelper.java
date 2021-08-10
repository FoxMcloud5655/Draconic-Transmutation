package draconictransmutation.utils;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableBiMap;

import draconictransmutation.DTCore;
import draconictransmutation.network.PacketHandler;
import draconictransmutation.network.packets.to_server.KeyPressPKT;
import draconictransmutation.utils.text.DTLang;
import draconictransmutation.utils.text.TextComponentUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DTCore.MODID, value = Dist.CLIENT)
public class ClientKeyHelper {

	private static ImmutableBiMap<KeyBinding, DTKeybind> mcToDT = ImmutableBiMap.of();
	private static ImmutableBiMap<DTKeybind, KeyBinding> DTToMc = ImmutableBiMap.of();

	@SubscribeEvent
	public static void keyPress(TickEvent.ClientTickEvent event) {
		for (KeyBinding k : mcToDT.keySet()) {
			while (k.isPressed()) {
				PacketHandler.sendToServer(new KeyPressPKT(mcToDT.get(k)));
			}
		}
	}

	public static void registerKeyBindings() {
		ImmutableBiMap.Builder<KeyBinding, DTKeybind> builder = ImmutableBiMap.builder();
		addKeyBinding(builder, DTKeybind.CHARGE, KeyModifier.NONE, GLFW.GLFW_KEY_V);
		addKeyBinding(builder, DTKeybind.EXTRA_FUNCTION, KeyModifier.NONE, GLFW.GLFW_KEY_C);
		addKeyBinding(builder, DTKeybind.MODE, KeyModifier.NONE, GLFW.GLFW_KEY_G);
		mcToDT = builder.build();
		DTToMc = mcToDT.inverse();
		for (KeyBinding k : mcToDT.keySet()) {
			ClientRegistry.registerKeyBinding(k);
		}
	}

	private static void addKeyBinding(ImmutableBiMap.Builder<KeyBinding, DTKeybind> builder, DTKeybind keyBind, KeyModifier modifier, int keyCode) {
		builder.put(new KeyBinding(keyBind.getTranslationKey(), KeyConflictContext.IN_GAME, modifier, InputMappings.Type.KEYSYM, keyCode,
						DTLang.DRACONICTRANSMUTATION.getTranslationKey()), keyBind);
	}

	public static ITextComponent getKeyName(DTKeybind k) {
		if (DTToMc.containsKey(k)) {
			return DTToMc.get(k).func_238171_j_();
		}
		//Fallback to the translation key of the key's function
		return TextComponentUtil.build(k);
	}
}