package draconictransmutation.gameObjs.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import draconictransmutation.api.capabilities.item.IItemCharge;
import draconictransmutation.capability.ChargeItemCapabilityWrapper;
import draconictransmutation.capability.ModeChangerItemCapabilityWrapper;
import draconictransmutation.utils.text.ILangEntry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public abstract class ItemMode extends ItemPE implements IItemMode, IItemCharge {

	private final int numCharge;
	private final ILangEntry[] modes;

	public ItemMode(Properties props, int numCharge, ILangEntry... modeDescrp) {
		super(props);
		this.numCharge = numCharge;
		this.modes = modeDescrp;
		addItemCapability(ChargeItemCapabilityWrapper::new);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.addInformation(stack, world, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1.0D - getChargePercent(stack);
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return numCharge;
	}
}