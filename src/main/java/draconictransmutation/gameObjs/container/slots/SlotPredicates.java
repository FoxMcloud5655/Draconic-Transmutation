package draconictransmutation.gameObjs.container.slots;

import java.util.function.Predicate;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.utils.EMCHelper;
import net.minecraft.item.ItemStack;

public final class SlotPredicates {
	public static final Predicate<ItemStack> HAS_EMC = input -> !input.isEmpty() && EMCHelper.doesItemHaveEmc(input);
	public static final Predicate<ItemStack> EMC_HOLDER = input -> !input.isEmpty() && input.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent();
	private SlotPredicates() {}
}