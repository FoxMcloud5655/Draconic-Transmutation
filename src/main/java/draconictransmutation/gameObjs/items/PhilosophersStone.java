package draconictransmutation.gameObjs.items;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import draconictransmutation.api.capabilities.item.IExtraFunction;
import draconictransmutation.capability.ExtraFunctionItemCapabilityWrapper;
import draconictransmutation.gameObjs.container.PhilosStoneContainer;
import draconictransmutation.gameObjs.registries.DTSoundEvents;
import draconictransmutation.utils.ClientKeyHelper;
import draconictransmutation.utils.DTKeybind;
import draconictransmutation.utils.PlayerHelper;
import draconictransmutation.utils.WorldTransmutations;
import draconictransmutation.utils.text.DTLang;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class PhilosophersStone extends ItemMode implements IExtraFunction {

	public PhilosophersStone(Properties props) {
		super(props, (byte) 4, DTLang.MODE_PHILOSOPHER_1, DTLang.MODE_PHILOSOPHER_2, DTLang.MODE_PHILOSOPHER_3);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return stack.copy();
	}

	public BlockRayTraceResult getHitBlock(PlayerEntity player) {
		return rayTrace(player.getEntityWorld(), player, player.isSneaking() ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		if (player == null) {
			return ActionResultType.FAIL;
		}
		BlockPos pos = ctx.getPos();
		Direction sideHit = ctx.getFace();
		World world = ctx.getWorld();
		ItemStack stack = ctx.getItem();

		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		BlockRayTraceResult rtr = getHitBlock(player);
		if (rtr.getType() == RayTraceResult.Type.BLOCK && !rtr.getPos().equals(pos)) {
			pos = rtr.getPos();
			sideHit = rtr.getFace();
		}
		Map<BlockPos, BlockState> toChange = getChanges(world, pos, player, sideHit, getMode(stack), getCharge(stack));
		if (!toChange.isEmpty()) {
			for (Map.Entry<BlockPos, BlockState> entry : toChange.entrySet()) {
				BlockPos currentPos = entry.getKey();
				PlayerHelper.checkedReplaceBlock((ServerPlayerEntity) player, currentPos, entry.getValue());
				if (world.rand.nextInt(8) == 0) {
					((ServerWorld) world).spawnParticle(ParticleTypes.LARGE_SMOKE, currentPos.getX(), currentPos.getY() + 1, currentPos.getZ(), 2, 0, 0, 0, 0);
				}
			}
			world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), DTSoundEvents.TRANSMUTE.get(), SoundCategory.PLAYERS, 1, 1);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		if (!player.getEntityWorld().isRemote) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack));
		}
		return true;
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.addInformation(stack, world, tooltips, flags);
		tooltips.add(DTLang.TOOLTIP_PHILOSTONE.translate(ClientKeyHelper.getKeyName(DTKeybind.EXTRA_FUNCTION)));
	}

	public static Map<BlockPos, BlockState> getChanges(World world, BlockPos pos, PlayerEntity player, Direction sideHit, int mode, int charge) {
		BlockState targeted = world.getBlockState(pos);
		boolean isSneaking = player.isSneaking();
		BlockState result = WorldTransmutations.getWorldTransmutation(targeted, isSneaking);
		if (result == null) {
			//Targeted block has no transmutations, no positions
			return Collections.emptyMap();
		}
		Stream<BlockPos> stream = null;
		switch (mode) {
			case 0: // Cube
				stream = BlockPos.getAllInBox(pos.add(-charge, -charge, -charge), pos.add(charge, charge, charge));
				break;
			case 1: // Panel
				if (sideHit == Direction.UP || sideHit == Direction.DOWN) {
					stream = BlockPos.getAllInBox(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge));
				} else if (sideHit == Direction.EAST || sideHit == Direction.WEST) {
					stream = BlockPos.getAllInBox(pos.add(0, -charge, -charge), pos.add(0, charge, charge));
				} else if (sideHit == Direction.SOUTH || sideHit == Direction.NORTH) {
					stream = BlockPos.getAllInBox(pos.add(-charge, -charge, 0), pos.add(charge, charge, 0));
				}
				break;
			case 2: // Line
				Direction playerFacing = player.getHorizontalFacing();
				if (playerFacing.getAxis() == Direction.Axis.Z) {
					stream = BlockPos.getAllInBox(pos.add(0, 0, -charge), pos.add(0, 0, charge));
				} else if (playerFacing.getAxis() == Direction.Axis.X) {
					stream = BlockPos.getAllInBox(pos.add(-charge, 0, 0), pos.add(charge, 0, 0));
				}
				break;
		}
		if (stream == null) {
			return Collections.emptyMap();
		}
		Map<BlockState, BlockState> conversions = new Object2ObjectArrayMap<>();
		conversions.put(targeted, result);
		Map<BlockPos, BlockState> changes = new HashMap<>();
		Block targetBlock = targeted.getBlock();
		stream.forEach(currentPos -> {
			BlockState state = world.getBlockState(currentPos);
			if (state.isIn(targetBlock)) {
				BlockState actualResult;
				if (conversions.containsKey(state)) {
					actualResult = conversions.get(state);
				} else {
					conversions.put(state, actualResult = WorldTransmutations.getWorldTransmutation(state, isSneaking));
				}
				//We allow for null keys to avoid having to look it up again from the world transmutations
				// which may be slightly slower, but we only add it as a position to change if we have a result
				if (actualResult != null) {
					changes.put(currentPos.toImmutable(), actualResult);
				}
			}
		});
		return changes;
	}

	private static class ContainerProvider implements INamedContainerProvider {

		private final ItemStack stack;

		private ContainerProvider(ItemStack stack) {
			this.stack = stack;
		}

		@Nonnull
		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
			return new PhilosStoneContainer(windowId, playerInventory, IWorldPosCallable.of(playerIn.getEntityWorld(), playerIn.getPosition()));
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return stack.getDisplayName();
		}
	}
}