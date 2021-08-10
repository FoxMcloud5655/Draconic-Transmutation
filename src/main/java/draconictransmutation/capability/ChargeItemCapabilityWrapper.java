package draconictransmutation.capability;

import javax.annotation.Nonnull;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.item.IItemCharge;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class ChargeItemCapabilityWrapper extends BasicItemCapability<IItemCharge> implements IItemCharge {

	@Override
	public Capability<IItemCharge> getCapability() {
		return ProjectEAPI.CHARGE_ITEM_CAPABILITY;
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return getItem().getNumCharges(stack);
	}
}