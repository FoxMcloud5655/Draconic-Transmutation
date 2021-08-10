package draconictransmutation;

import draconictransmutation.client.DTBlockStateProvider;
import draconictransmutation.client.DTItemModelProvider;
import draconictransmutation.client.lang.DTLangProvider;
import draconictransmutation.client.sound.DTSoundProvider;
import draconictransmutation.common.DTAdvancementsProvider;
import draconictransmutation.common.DTCustomConversionProvider;
import draconictransmutation.common.loot.DTLootProvider;
import draconictransmutation.common.recipe.DTRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = DTCore.MODID, bus = Bus.MOD)
public class DTDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		if (event.includeClient()) {
			//Client side data generators
			gen.addProvider(new DTLangProvider(gen));
			gen.addProvider(new DTSoundProvider(gen, existingFileHelper));
			gen.addProvider(new DTBlockStateProvider(gen, existingFileHelper));
			gen.addProvider(new DTItemModelProvider(gen, existingFileHelper));
		}
		if (event.includeServer()) {
			gen.addProvider(new DTAdvancementsProvider(gen));
			gen.addProvider(new DTLootProvider(gen));
			gen.addProvider(new DTRecipeProvider(gen));
			gen.addProvider(new DTCustomConversionProvider(gen));
		}
	}
}