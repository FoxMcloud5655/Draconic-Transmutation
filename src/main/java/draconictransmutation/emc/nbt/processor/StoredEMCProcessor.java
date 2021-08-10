package draconictransmutation.emc.nbt.processor;

import javax.annotation.Nonnull;

import draconictransmutation.api.ItemInfo;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.nbt.INBTProcessor;
import draconictransmutation.api.nbt.NBTProcessor;
import net.minecraft.item.ItemStack;

@NBTProcessor
public class StoredEMCProcessor implements INBTProcessor {

	@Override
	public String getName() {
		return "StoredEMCProcessor";
	}

	@Override
	public String getDescription() {
		return "Increases the EMC value of the item to take into account any EMC the item has stored.";
	}

	@Override
	public long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException {
		ItemStack stack = info.createStack();
		return stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).map(emcHolder -> Math.addExact(currentEMC, emcHolder.getStoredEmc(stack))).orElse(currentEMC);
	}
}