package draconictransmutation.common;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registries.DTBlocks;
import draconictransmutation.gameObjs.registries.DTItems;
import draconictransmutation.utils.text.DTLang;
import draconictransmutation.utils.text.ILangEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

//Vanilla copy of Advancement provider, slightly modified to be more friendly/usable by us
public class DTAdvancementsProvider implements IDataProvider {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private final DataGenerator generator;

	public DTAdvancementsProvider(DataGenerator generatorIn) {
		this.generator = generatorIn;
	}

	@Override
	public void act(@Nonnull DirectoryCache cache) {
		Path outputFolder = this.generator.getOutputFolder();
		Set<ResourceLocation> set = new HashSet<>();
		addAdvancements(advancement -> {
			if (set.add(advancement.getId())) {
				Path path = getPath(outputFolder, advancement);
				try {
					IDataProvider.save(GSON, cache, advancement.copy().serialize(), path);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save advancement {}", path, ioexception);
				}
			} else {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			}
		});
	}

	private static Path getPath(Path path, Advancement advancement) {
		return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
	}

	@Nonnull
	@Override
	public String getName() {
		return "Advancements";
	}

	private void addAdvancements(Consumer<Advancement> advancementConsumer) {
		Advancement root = Advancement.Builder.builder()
				.withDisplay(DTItems.PHILOSOPHERS_STONE,
						DTLang.DRACONICTRANSMUTATION.translate(),
						DTLang.ADVANCEMENTS_DT_DESCRIPTION.translate(),
						new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
						FrameType.TASK,
						false,
						false,
						false)
				.withCriterion("philstone_recipe", InventoryChangeTrigger.Instance.forItems(Items.GLOWSTONE_DUST, Items.DIAMOND, Items.REDSTONE))
				.register(advancementConsumer, DTCore.rl("root").toString());
		addTransmutation(advancementConsumer, root);
	}

	private static Advancement.Builder childDisplay(Advancement parent, IItemProvider icon, ILangEntry title, ILangEntry description) {
		return Advancement.Builder.builder()
				.withParent(parent)
				.withDisplay(icon, title.translate(), description.translate(), null, FrameType.TASK, true, true, false);
	}

	private void addTransmutation(Consumer<Advancement> advancementConsumer, Advancement parent) {
		Advancement root = childDisplay(parent, DTItems.PHILOSOPHERS_STONE, DTLang.ADVANCEMENTS_PHILO_STONE, DTLang.ADVANCEMENTS_PHILO_STONE_DESCRIPTION)
				.withCriterion("philosophers_stone", InventoryChangeTrigger.Instance.forItems(DTItems.PHILOSOPHERS_STONE))
				.register(advancementConsumer, DTCore.rl("philosophers_stone").toString());
		//Branch 1
		Advancement transmutationTable = childDisplay(root, DTBlocks.TRANSMUTATION_TABLE, DTLang.ADVANCEMENTS_TRANSMUTATION_TABLE,
				DTLang.ADVANCEMENTS_TRANSMUTATION_TABLE_DESCRIPTION)
				.withCriterion("trans_table", InventoryChangeTrigger.Instance.forItems(DTBlocks.TRANSMUTATION_TABLE))
				.register(advancementConsumer, DTCore.rl("transmutation_table").toString());
		childDisplay(transmutationTable, DTItems.TRANSMUTATION_TABLET, DTLang.ADVANCEMENTS_TRANSMUTATION_TABLET, DTLang.ADVANCEMENTS_TRANSMUTATION_TABLET_DESCRIPTION)
				.withCriterion("trans_tablet", InventoryChangeTrigger.Instance.forItems(DTItems.TRANSMUTATION_TABLET))
				.register(advancementConsumer, DTCore.rl("transmutation_tablet").toString());
		//Branch 2
	}
}