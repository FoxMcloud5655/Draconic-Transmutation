package draconictransmutation.client.sound;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registries.DTSoundEvents;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DTSoundProvider extends BaseSoundProvider {

	public DTSoundProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, existingFileHelper, DTCore.MODID);
	}

	@Override
	protected void addSoundEvents() {
		addSoundEventWithSubtitle(DTSoundEvents.WIND_MAGIC, DTCore.rl("item/pewindmagic"));
		addSoundEventWithSubtitle(DTSoundEvents.WATER_MAGIC, DTCore.rl("item/pewatermagic"));
		addSoundEventWithSubtitle(DTSoundEvents.POWER, DTCore.rl("item/pepower"));
		addSoundEventWithSubtitle(DTSoundEvents.HEAL, DTCore.rl("item/peheal"));
		addSoundEventWithSubtitle(DTSoundEvents.DESTRUCT, DTCore.rl("item/pedestruct"));
		addSoundEventWithSubtitle(DTSoundEvents.CHARGE, DTCore.rl("item/pecharge"));
		addSoundEventWithSubtitle(DTSoundEvents.UNCHARGE, DTCore.rl("item/peuncharge"));
		addSoundEventWithSubtitle(DTSoundEvents.TRANSMUTE, DTCore.rl("item/petransmute"));
		//TODO: Evaluate the remaining sounds that we don't actually use anywhere
	}
}