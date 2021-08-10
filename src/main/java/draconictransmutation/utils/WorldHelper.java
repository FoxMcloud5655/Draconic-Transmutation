package draconictransmutation.utils;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registries.DTSoundEvents;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.NetherrackBlock;
import net.minecraft.block.NyliumBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Helper class for anything that touches a World. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper {
	public static void createLootDrop(List<ItemStack> drops, World world, BlockPos pos) {
		createLootDrop(drops, world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void createLootDrop(List<ItemStack> drops, World world, double x, double y, double z) {
		if (!drops.isEmpty()) {
			ItemHelper.compactItemListNoStacksize(drops);
			for (ItemStack drop : drops) {
				world.addEntity(new ItemEntity(world, x, y, z, drop));
			}
		}
	}

	public static void drainFluid(World world, BlockPos pos, BlockState state, Fluid toMatch) {
		Block block = state.getBlock();
		if (block instanceof IFluidBlock && ((IFluidBlock) block).getFluid().isEquivalentTo(toMatch)) {
			//If it is a fluid block drain it (may be the case for some custom block?)
			// We double check though the fluid block represents a given one though, in case there is some weird thing
			// going on and we are a bucket pickup handler for the actual water and fluid state
			((IFluidBlock) block).drain(world, pos, FluidAction.EXECUTE);
		} else if (block instanceof IBucketPickupHandler) {
			//If it is a bucket pickup handler (so may be a fluid logged block) "pick it up"
			// This includes normal fluid blocks
			((IBucketPickupHandler) block).pickupFluid(world, pos, state);
		}
	}

	public static void dropInventory(IItemHandler inv, World world, BlockPos pos) {
		if (inv == null) {
			return;
		}
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				ItemEntity ent = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ());
				ent.setItem(stack);
				world.addEntity(ent);
			}
		}
	}

	public static void extinguishNearby(World world, PlayerEntity player) {
		BlockPos.getAllInBox(player.getPosition().add(-1, -1, -1), player.getPosition().add(1, 1, 1)).forEach(pos -> {
			pos = pos.toImmutable();
			if (world.getBlockState(pos).getBlock() == Blocks.FIRE && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				world.removeBlock(pos, false);
			}
		});
	}

	/**
	 * Checks if a block is a {@link ILiquidContainer} that supports a specific fluid type.
	 */
	public static boolean isLiquidContainerForFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getBlock() instanceof ILiquidContainer && ((ILiquidContainer) state.getBlock()).canContainFluid(world, pos, state, fluid);
	}

	/**
	 * Attempts to place a fluid in a specific spot if the spot is a {@link ILiquidContainer} that supports the fluid otherwise try to place it in the block that is on
	 * the given side of the clicked block.
	 */
	public static void placeFluid(@Nullable ServerPlayerEntity player, World world, BlockPos pos, Direction sideHit, FlowingFluid fluid, boolean checkWaterVaporize) {
		if (isLiquidContainerForFluid(world, pos, world.getBlockState(pos), fluid)) {
			//If the spot can be logged with our fluid then try using the position directly
			placeFluid(player, world, pos, fluid, checkWaterVaporize);
		} else {
			//Otherwise offset it because we clicked against the block
			placeFluid(player, world, pos.offset(sideHit), fluid, checkWaterVaporize);
		}
	}

	/**
	 * Attempts to place a fluid in a specific spot, if the spot is a {@link ILiquidContainer} that supports the fluid, insert it instead.
	 *
	 * @apiNote Call this from the server side
	 */
	public static void placeFluid(@Nullable ServerPlayerEntity player, World world, BlockPos pos, FlowingFluid fluid, boolean checkWaterVaporize) {
		BlockState blockState = world.getBlockState(pos);
		if (checkWaterVaporize && world.getDimensionType().isUltrawarm() && fluid.isIn(FluidTags.WATER)) {
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			for (int l = 0; l < 8; ++l) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
			}
		} else if (isLiquidContainerForFluid(world, pos, blockState, fluid)) {
			((ILiquidContainer) blockState.getBlock()).receiveFluid(world, pos, blockState, fluid.getStillFluidState(false));
		} else {
			Material material = blockState.getMaterial();
			if ((!material.isSolid() || material.isReplaceable()) && !material.isLiquid()) {
				world.destroyBlock(pos, true);
			}
			if (player == null) {
				world.setBlockState(pos, fluid.getDefaultState().getBlockState());
			} else {
				PlayerHelper.checkedPlaceBlock(player, pos, fluid.getDefaultState().getBlockState());
			}
		}
	}

	/**
	 * Gets an ItemHandler of a specific tile from the given side. Falls back to using wrappers if the tile is an instance of an ISidedInventory/IInventory.
	 */
	@Nullable
	public static IItemHandler getItemHandler(@Nonnull TileEntity tile, @Nullable Direction direction) {
		Optional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve();
		if (capability.isPresent()) {
			return capability.get();
		} else if (tile instanceof ISidedInventory) {
			return new SidedInvWrapper((ISidedInventory) tile, direction);
		} else if (tile instanceof IInventory) {
			return new InvWrapper((IInventory) tile);
		}
		return null;
	}

	private static boolean isGrassLikeBlock(Block crop) {
		//Note: We count netherrack like a grass like block as it propagates growing to neighboring nylium blocks
		// and its can grow methods behave like one
		return crop instanceof GrassBlock || crop instanceof NyliumBlock || crop instanceof NetherrackBlock;
	}

	/**
	 * Breaks and "harvests" a block if the player has permission to break it or there is no player
	 */
	private static void harvestBlock(World world, BlockPos pos, @Nullable ServerPlayerEntity player) {
		if (player == null || PlayerHelper.hasBreakPermission(player, pos)) {
			world.destroyBlock(pos, true, player);
		}
	}


	public static void igniteNearby(World world, PlayerEntity player) {
		for (BlockPos pos : BlockPos.getAllInBoxMutable(player.getPosition().add(-8, -5, -8), player.getPosition().add(8, 5, 8))) {
			if (world.rand.nextInt(128) == 0 && world.isAirBlock(pos)) {
				PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) player, pos.toImmutable(), Blocks.FIRE.getDefaultState());
			}
		}
	}

	@Nonnull
	public static ActionResultType igniteBlock(ItemUseContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		if (player == null) {
			return ActionResultType.FAIL;
		}
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		Direction side = ctx.getFace();
		BlockState state = world.getBlockState(pos);
		if (AbstractFireBlock.canLightBlock(world, pos, side)) {
			if (!world.isRemote && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				world.setBlockState(pos, AbstractFireBlock.getFireForPlacement(world, pos));
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), DTSoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		} else if (CampfireBlock.canBeLit(state)) {
			if (!world.isRemote && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				world.setBlockState(pos, state.with(BlockStateProperties.LIT, true));
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), DTSoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		} else if (state.isFlammable(world, pos, side)) {
			if (!world.isRemote && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				// Ignite the block
				state.catchFire(world, pos, side, player);
				if (state.getBlock() instanceof TNTBlock) {
					world.removeBlock(pos, false);
				}
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), DTSoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		} else {
			return ActionResultType.PASS;
		}
		return ActionResultType.SUCCESS;
	}

	/**
	 * Checks if a position is in bounds of the world, and is loaded
	 *
	 * @param world world
	 * @param pos   position
	 *
	 * @return True if the position is loaded or the given world is of a superclass of IWorldReader that does not have a concept of being loaded.
	 *
	 * @implNote From Mekanism
	 */
	public static boolean isBlockLoaded(@Nullable IBlockReader world, @Nonnull BlockPos pos) {
		if (world == null || !World.isValid(pos)) {
			return false;
		} else if (world instanceof IWorldReader) {
			//Note: We don't bother checking if it is a world and then isBlockPresent because
			// all that does is also validate the y value is in bounds, and we already check to make
			// sure the position is valid both in the y and xz directions
			return ((IWorldReader) world).isBlockLoaded(pos);
		}
		return true;
	}

	/**
	 * Gets a tile entity if the location is loaded
	 *
	 * @param world world
	 * @param pos   position
	 *
	 * @return tile entity if found, null if either not found or not loaded
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static TileEntity getTileEntity(@Nullable IBlockReader world, @Nonnull BlockPos pos) {
		if (!isBlockLoaded(world, pos)) {
			//If the world is null or its a world reader and the block is not loaded, return null
			return null;
		}
		return world.getTileEntity(pos);
	}

	/**
	 * Gets a tile entity if the location is loaded
	 *
	 * @param clazz Class type of the TileEntity we expect to be in the position
	 * @param world world
	 * @param pos   position
	 *
	 * @return tile entity if found, null if either not found, not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <T extends TileEntity> T getTileEntity(@Nonnull Class<T> clazz, @Nullable IBlockReader world, @Nonnull BlockPos pos) {
		return getTileEntity(clazz, world, pos, false);
	}

	/**
	 * Gets a tile entity if the location is loaded
	 *
	 * @param clazz        Class type of the TileEntity we expect to be in the position
	 * @param world        world
	 * @param pos          position
	 * @param logWrongType Whether or not an error should be logged if a tile of a different type is found at the position
	 *
	 * @return tile entity if found, null if either not found or not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <T extends TileEntity> T getTileEntity(@Nonnull Class<T> clazz, @Nullable IBlockReader world, @Nonnull BlockPos pos, boolean logWrongType) {
		TileEntity tile = getTileEntity(world, pos);
		if (tile == null) {
			return null;
		}
		if (clazz.isInstance(tile)) {
			return clazz.cast(tile);
		} else if (logWrongType) {
			DTCore.LOGGER.warn("Unexpected TileEntity class at {}, expected {}, but found: {}", pos, clazz, tile.getClass());
		}
		return null;
	}
}