package draconictransmutation.emc.nbt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import draconictransmutation.api.ItemInfo;
import draconictransmutation.api.nbt.INBTProcessor;
import draconictransmutation.config.NBTProcessorConfig;
import draconictransmutation.emc.EMCMappingHandler;
import draconictransmutation.gameObjs.DTTags;
import draconictransmutation.utils.AnnotationHelper;
import draconictransmutation.utils.ItemHelper;
import net.minecraft.nbt.CompoundNBT;

public class NBTManager {

	private static final List<INBTProcessor> processors = new ArrayList<>();

	public static void loadProcessors() {
		if (processors.isEmpty()) {
			processors.addAll(AnnotationHelper.getNBTProcessors());
			//Setup the config for the NBT Processors
			NBTProcessorConfig.setup(processors);
		}
	}

	@Nonnull
	public static ItemInfo getPersistentInfo(@Nonnull ItemInfo info) {
		if (!info.hasNBT() || info.getItem().isIn(DTTags.Items.NBT_WHITELIST) || EMCMappingHandler.hasEmcValue(info)) {
			//If we have no NBT, we want to allow the tag to be kept, or we have an exact match to a stored value just go with it
			return info;
		}
		//Cleans up the tag in info to reduce it as much as possible
		List<CompoundNBT> persistentNBT = new ArrayList<>();
		for (INBTProcessor processor : processors) {
			if (NBTProcessorConfig.isEnabled(processor) && processor.hasPersistentNBT() && NBTProcessorConfig.hasPersistent(processor)) {
				CompoundNBT nbt = processor.getPersistentNBT(info);
				if (nbt != null) {
					persistentNBT.add(nbt);
				}
			}
		}
		return ItemInfo.fromItem(info.getItem(), ItemHelper.recombineNBT(persistentNBT));
	}

	public static long getEmcValue(@Nonnull ItemInfo info) {
		//TODO: Fix this, as it does not catch the edge case that we have an exact match and then there is random added NBT on top of it
		// but that can be thought about more once we have the first pass complete. For example if someone put an enchantment on a potion
		long emcValue = EMCMappingHandler.getStoredEmcValue(info);
		if (!info.hasNBT()) {
			//If our info has no NBT anyways just return based on the value we got for it
			return emcValue;
		} else if (emcValue == 0) {
			//Try getting a base emc value from the NBT less variant if we don't have one matching our NBT
			emcValue = EMCMappingHandler.getStoredEmcValue(ItemInfo.fromItem(info.getItem()));
			if (emcValue == 0) {
				//The base item doesn't have an EMC value either so just exit
				return 0;
			}
		}

		//Note: We continue to use our initial ItemInfo so that we are calculating based on the NBT
		for (INBTProcessor processor : processors) {
			if (NBTProcessorConfig.isEnabled(processor)) {
				try {
					emcValue = processor.recalculateEMC(info, emcValue);
				} catch (ArithmeticException e) {
					//Return the last successfully calculated EMC value
					return emcValue;
				}
				if (emcValue <= 0) {
					//Exit if it gets to zero (also safety check for less than zero in case a mod didn't bother sanctifying their data)
					return 0;
				}
			}
		}
		return emcValue;
	}
}