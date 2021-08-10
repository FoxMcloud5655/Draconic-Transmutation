package draconictransmutation.common.tag;

import javax.annotation.Nullable;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.DTTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DTItemTagsProvider extends ItemTagsProvider {

	public DTItemTagsProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTagsProvider, DTCore.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(DTTags.Items.NBT_WHITELIST);
	}
}