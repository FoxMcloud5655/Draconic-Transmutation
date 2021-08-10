package draconictransmutation.gameObjs.registration.impl;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registration.WrappedDeferredRegister;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEventDeferredRegister extends WrappedDeferredRegister<SoundEvent> {

	public SoundEventDeferredRegister() {
		super(ForgeRegistries.SOUND_EVENTS);
	}

	public SoundEventRegistryObject<SoundEvent> register(String name) {
		return register(name, () -> new SoundEvent(DTCore.rl(name)), SoundEventRegistryObject::new);
	}
}