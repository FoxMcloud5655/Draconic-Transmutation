package draconictransmutation.gameObjs.items;

import javax.annotation.Nonnull;

import draconictransmutation.gameObjs.container.TransmutationContainer;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TransmutationTablet extends ItemPE {

	public TransmutationTablet(Properties props) {
		super(props);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
		if (!world.isRemote) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(hand), buf -> buf.writeEnumValue(hand));
		}
		return ActionResult.resultSuccess(player.getHeldItem(hand));
	}

	private static class ContainerProvider implements INamedContainerProvider {

		private final Hand hand;

		private ContainerProvider(Hand hand) {
			this.hand = hand;
		}

		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
			return new TransmutationContainer(windowId, playerInventory, hand);
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return DTLang.TRANSMUTATION_TRANSMUTE.translate();
		}
	}
}