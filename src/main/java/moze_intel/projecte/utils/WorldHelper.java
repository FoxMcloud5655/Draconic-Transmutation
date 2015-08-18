package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Helper class for anything that touches a World.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper
{
	@SuppressWarnings("unchecked")
	public static final ImmutableList<Class<? extends EntityLiving>> peacefuls = ImmutableList.<Class<? extends EntityLiving>>of(
			EntitySheep.class, EntityPig.class, EntityCow.class,
			EntityMooshroom.class, EntityChicken.class, EntityBat.class,
			EntityVillager.class, EntitySquid.class, EntityOcelot.class,
			EntityWolf.class, EntityHorse.class, EntityRabbit.class
	);

	@SuppressWarnings("unchecked")
	public static final ImmutableList<Class<? extends EntityLiving>> mobs = ImmutableList.<Class<? extends EntityLiving>>of(
			EntityZombie.class, EntitySkeleton.class, EntityCreeper.class,
			EntitySpider.class, EntityEnderman.class, EntitySilverfish.class,
			EntityPigZombie.class, EntityGhast.class, EntityBlaze.class,
			EntitySlime.class, EntityWitch.class, EntityRabbit.class, EntityEndermite.class
	);

	public static Set<Class<? extends Entity>> interdictionBlacklist = Sets.newHashSet();

	public static Set<Class<? extends Entity>> swrgBlacklist = Sets.newHashSet();

	public static boolean blacklistInterdiction(Class<? extends Entity> clazz)
	{
		if (!interdictionBlacklist.contains(clazz))
		{
			interdictionBlacklist.add(clazz);
			return true;
		}
		return false;
	}

	public static boolean blacklistSwrg(Class<? extends Entity> clazz)
	{
		if (!interdictionBlacklist.contains(clazz))
		{
			interdictionBlacklist.add(clazz);
			return true;
		}
		return false;
	}

	public static void createLootDrop(List<ItemStack> drops, World world, BlockPos pos)
	{
		createLootDrop(drops, world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void createLootDrop(List<ItemStack> drops, World world, double x, double y, double z)
	{
		if (drops.isEmpty())
		{
			return;
		}

		ItemHelper.compactItemList(drops);

		if (ProjectEConfig.useLootBalls)
		{
			world.spawnEntityInWorld(new EntityLootBall(world, drops, x, y, z));
		}
		else
		{
			for (ItemStack drop : drops)
			{
				spawnEntityItem(world, drop, x, y, z);
			}
		}
	}
	
	public static void extinguishNearby(World world, EntityPlayer player)
	{
		for (BlockPos pos : getPositionsFromCorners(new BlockPos(player).add(-1, -1, -1), new BlockPos(player).add(1, 1, 1)))
		{
			if (world.getBlockState(pos).getBlock() == Blocks.fire)
			{
				world.setBlockToAir(pos);
			}
		}
	}
	
	public static void freezeNearby(World world, EntityPlayer player)
	{
		freezeInBoundingBox(world, player.getEntityBoundingBox().expand(5, 5, 5));
	}
	
	public static void freezeInBoundingBox(World world, AxisAlignedBB box)
	{
		for (BlockPos pos : getPositionsFromBox(box))
		{
			Block b = world.getBlockState(pos).getBlock();
			if (b == Blocks.water || b == Blocks.flowing_water)
			{
				world.setBlockState(pos, Blocks.ice.getDefaultState());
			}
			else if (b.isSideSolid(world, pos, EnumFacing.UP))
			{
				if (world.isAirBlock(pos.up()))
				{
					world.setBlockState(pos.up(), Blocks.snow_layer.getDefaultState());
				}
			}
		}
	}
		
	public static void freezeNearbyRandomly(World world, EntityPlayer player)
	{
		freezeInBoundingBoxRandomly(world, player.getEntityBoundingBox().expand(5, 5, 5));
	}
	
	public static void freezeInBoundingBoxRandomly(World world, AxisAlignedBB box)
	{
		for (BlockPos pos : getPositionsFromBox(box))
		{
			Block b = world.getBlockState(pos).getBlock();

			if ((b == Blocks.water || b == Blocks.flowing_water) && world.rand.nextInt(128) == 0)
			{
				world.setBlockState(pos, Blocks.ice.getDefaultState());
			}
			else if (b.isSideSolid(world, pos, EnumFacing.UP))
			{
				if (world.isAirBlock(pos.up()) && world.rand.nextInt(128) == 0)
				{
					world.setBlockState(pos.up(), Blocks.snow_layer.getDefaultState());
				}
			}
		}
	}
	
	public static List<TileEntity> getAdjacentTileEntities(World world, TileEntity tile)
	{
		List<TileEntity> list = Lists.newArrayList();
		for (EnumFacing e : EnumFacing.VALUES)
		{
			TileEntity t = world.getTileEntity(tile.getPos().offset(e));
			if (t != null)
			{
				list.add(t);
			}
		}
		return list;
	}

	public static List<ItemStack> getBlockDrops(World world, EntityPlayer player, IBlockState state, ItemStack stack, BlockPos pos)
	{
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0 && state.getBlock().canSilkHarvest(world, pos, state, player))
		{
			return Lists.newArrayList(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
		}

		return state.getBlock().getDrops(world, pos, state, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AxisAlignedBB getBroadDeepBox(BlockPos pos, EnumFacing direction, int offset)
	{
		switch (direction)
		{
			case EAST: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX(), pos.getY() + offset, pos.getZ() + offset);
			case WEST: return new AxisAlignedBB(pos.getX(), pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case UP: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
			case DOWN: return new AxisAlignedBB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case SOUTH: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ());
			case NORTH: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ(), pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			default: return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	public static AxisAlignedBB getDeepBox(BlockPos pos, EnumFacing direction, int depth)
	{
		switch (direction)
		{
			case EAST: return new AxisAlignedBB(pos.getX() - depth, pos.getY() - 1, pos.getZ() - 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1);
			case WEST: return new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ() - 1, pos.getX() + depth, pos.getY() + 1, pos.getZ() + 1);
			case UP: return new AxisAlignedBB(pos.getX() - 1, pos.getY() - depth, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
			case DOWN: return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + depth, pos.getZ() + 1);
			case SOUTH: return new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - depth, pos.getX() + 1, pos.getY() + 1, pos.getZ());
			case NORTH: return new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + depth);
			default: return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Gets an AABB for AOE digging operations. The charge increases only the breadth of the box.
	 * Y level remains constant. As such, a direction hit is unneeded.
	 */
	public static AxisAlignedBB getFlatYBox(BlockPos pos, int offset)
	{
		return new AxisAlignedBB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
	}

	public static <T extends Entity> T getNewEntityInstance(Class<T> c, World world)
	{
		try
		{
			Constructor<T> constr = c.getConstructor(World.class);
			T ent = constr.newInstance(world);

			if (ent instanceof EntitySkeleton)
			{
				if (world.rand.nextInt(2) == 0)
				{
					((EntitySkeleton) ent).setSkeletonType(1);
					ent.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
				}
				else
				{
					ent.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
				}
			}
			else if (ent instanceof EntityPigZombie)
			{
				ent.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
			}
			else if (ent instanceof EntitySheep)
			{
				((EntitySheep) ent).setFleeceColor(EnumDyeColor.byMetadata(MathUtils.randomIntInRange(0, 15)));
			}
			else if (ent instanceof EntityVillager)
			{
				VillagerRegistry.setRandomProfession(((EntityVillager) ent), world.rand);
			}
			else if (ent instanceof EntityRabbit)
			{
				((EntityRabbit) ent).setRabbitType(world.rand.nextInt(6));
			}
			else if (ent instanceof EntityHorse)
			{
				((EntityHorse) ent).setHorseType(MathUtils.randomIntInRange(0, 2));
				if (((EntityHorse) ent).getHorseType() == 0)
				{
					((EntityHorse) ent).setHorseVariant(MathUtils.randomIntInRange(0, 6));
				}
			}

			return ent;
		}
		catch (Exception e)
		{
			PELogger.logFatal("Could not create new entity instance for: "+c.getCanonicalName());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Wrapper around BlockPos.getAllInBox() with an AABB
	 */
	@SuppressWarnings("unchecked")
	public static Iterable<BlockPos> getPositionsFromBox(AxisAlignedBB box)
	{
		return BlockPos.getAllInBox(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ));
	}

	/**
	 * No-warnings wrapper around BlockPos.getAllInBox
	 */
	@SuppressWarnings("unchecked")
	public static Iterable<BlockPos> getPositionsFromCorners(BlockPos corner1, BlockPos corner2)
	{
		return BlockPos.getAllInBox(corner1, corner2);
	}

	public static EntityLiving getRandomEntity(World world, EntityLiving toRandomize)
	{
		Class<? extends EntityLiving> entClass = toRandomize.getClass();

		if (peacefuls.contains(entClass))
		{
			return getNewEntityInstance(CollectionHelper.getRandomListEntry(peacefuls, entClass), world);
		}
		else if (mobs.contains(entClass))
		{
			EntityLiving ent = getNewEntityInstance(CollectionHelper.getRandomListEntry(mobs, entClass), world);
			if (ent instanceof EntityRabbit)
			{
				((EntityRabbit) ent).setRabbitType(99);
			}
			return ent;
		}
		else if (world.rand.nextInt(2) == 0)
		{
			return getNewEntityInstance(EntitySlime.class, world);
		}
		else
		{
			return getNewEntityInstance(EntitySheep.class, world);
		}
	}

	public static List<TileEntity> getTileEntitiesWithinAABB(World world, AxisAlignedBB bBox)
	{
		List<TileEntity> list = Lists.newArrayList();

		for (BlockPos pos : getPositionsFromBox(bBox))
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null)
			{
				list.add(tile);
			}
		}

		return list;
	}

	/**
	 * Gravitates an entity, vanilla xp orb style, towards a position
	 * Code adapted from EntityXPOrb and OpenBlocks Vacuum Hopper, mostly the former
	 */
	public static void gravitateEntityTowards(Entity ent, double x, double y, double z)
	{
		double dX = x - ent.posX;
		double dY = y - ent.posY;
		double dZ = z - ent.posZ;
		double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

		double vel = 1.0 - dist / 15.0;
		if (vel > 0.0D)
		{
			vel *= vel;
			ent.motionX += dX / dist * vel * 0.05;
			ent.motionY += dY / dist * vel * 0.1;
			ent.motionZ += dZ / dist * vel * 0.05;
			ent.moveEntity(ent.motionX, ent.motionY, ent.motionZ);
		}
	}
	
	public static void growNearbyRandomly(boolean harvest, World world, BlockPos pos)
	{
		int chance = harvest ? 16 : 32;

		for (BlockPos currentPos : getPositionsFromCorners(pos.add(-5, -3, -5), pos.add(5, 3, 5)))
		{
			IBlockState state = world.getBlockState(currentPos);
			Block crop = state.getBlock();

			// Vines, leaves, tallgrass, deadbush, doubleplants
			if (crop instanceof IShearable)
			{
				if (harvest)
				{
					world.destroyBlock(currentPos, true);
				}
			}
			// Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
			// Mushroom, potato, sapling, stems, tallgrass
			else if (crop instanceof IGrowable)
			{
				IGrowable growable = (IGrowable) crop;
				if(harvest && !growable.canGrow(world, currentPos, state, false))
				{
					world.destroyBlock(currentPos, true);
				}
				else if (world.rand.nextInt(chance) == 0)
				{
					if (ProjectEConfig.harvBandGrass || !crop.getUnlocalizedName().toLowerCase().contains("grass"))
					{
						growable.grow(world, world.rand, currentPos, state);
					}
				}
			}
			// All modded
			// Cactus, Reeds, Netherwart, Flower
			else if (crop instanceof IPlantable)
			{
				if (world.rand.nextInt(chance / 4) == 0)
				{
					for (int i = 0; i < (harvest ? 8 : 4); i++)
					{
						crop.updateTick(world, currentPos, world.getBlockState(currentPos), world.rand);
					}
				}

				if (harvest)
				{
					if (crop instanceof BlockFlower)
					{
						world.destroyBlock(pos, true);
					}
					if (crop == Blocks.reeds || crop == Blocks.cactus)
					{
						boolean shouldHarvest = true;

						for (int i = 1; i < 3; i++)
						{
							if (world.getBlockState(pos.up()).getBlock() != crop)
							{
								shouldHarvest = false;
								break;
							}
						}

						if (shouldHarvest)
						{
							for (int i = crop == Blocks.reeds ? 1 : 0; i < 3; i++)
							{
								world.destroyBlock(pos.offset(EnumFacing.UP, i), true);
							}
						}
					}
					if (crop == Blocks.nether_wart)
					{
						int growth = ((Integer) world.getBlockState(pos).getValue(BlockNetherWart.AGE));
						if (growth == 3)
						{
							world.destroyBlock(pos, true);
						}
					}
				}
			}
		}
	}

	public static void growNearbyRandomly(boolean harvest, World world, Entity player)
	{
		growNearbyRandomly(harvest, world, new BlockPos(player.posX, player.posY, player.posZ));
	}

	/**
	 * Recursively mines out a vein of the given Block, starting from the provided coordinates
	 */
	public static void harvestVein(World world, EntityPlayer player, ItemStack stack, BlockPos pos, IBlockState target, List<ItemStack> currentDrops, int numMined)
	{
		if (numMined >= Constants.MAX_VEIN_SIZE)
		{
			return;
		}

		AxisAlignedBB b = new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

		for (BlockPos currentPos : getPositionsFromBox(b))
		{
			IBlockState currentState = world.getBlockState(currentPos);

			if (currentState == target || (target.getBlock() == Blocks.lit_redstone_ore && currentState.getBlock() == Blocks.redstone_ore))
			{
				currentDrops.addAll(getBlockDrops(world, player, currentState, stack, pos));
				world.setBlockToAir(pos);
				player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(currentState.getBlock())], 1);
				numMined++;
				harvestVein(world, player, stack, currentPos, target, currentDrops, numMined);
			}
		}
	}
	
	public static void igniteNearby(World world, EntityPlayer player)
	{
		for (BlockPos pos : getPositionsFromCorners(new BlockPos(player).add(-8, -5, -8), new BlockPos(player).add(8, 5, 8)))
		{
			if (world.rand.nextInt(128) == 0 && world.isAirBlock(pos))
			{
				world.setBlockState(pos, Blocks.fire.getDefaultState());
			}
		}
	}

	public static boolean isArrowInGround(EntityArrow arrow)
	{
		return ReflectionHelper.getArrowInGround(arrow);
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given point
	 */
	public static void repelEntitiesInAABBFromPoint(World world, AxisAlignedBB effectBounds, double x, double y, double z, boolean isSWRG)
	{
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, effectBounds);

		for (Entity ent : list)
		{
			if ((isSWRG && !swrgBlacklist.contains(ent.getClass()))
					|| (!isSWRG && !interdictionBlacklist.contains(ent.getClass()))) {
				if ((ent instanceof EntityLiving) || (ent instanceof IProjectile))
				{
					if (!isSWRG && ProjectEConfig.interdictionMode && !(ent instanceof IMob || ent instanceof IProjectile))
					{
						continue;
					}
					else
					{
						if (ent instanceof EntityArrow && ((EntityArrow) ent).onGround)
						{
							continue;
						}
						Vec3 p = new Vec3(x, y, z);
						Vec3 t = new Vec3(ent.posX, ent.posY, ent.posZ);
						double distance = p.distanceTo(t) + 0.1D;

						Vec3 r = new Vec3(t.xCoord - p.xCoord, t.yCoord - p.yCoord, t.zCoord - p.zCoord);

						ent.motionX += r.xCoord / 1.5D / distance;
						ent.motionY += r.yCoord / 1.5D / distance;
						ent.motionZ += r.zCoord / 1.5D / distance;
					}
				}
			}
		}
	}

	public static void spawnEntityItem(World world, ItemStack stack, BlockPos pos)
	{
		spawnEntityItem(world, stack, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void spawnEntityItem(World world, ItemStack stack, double x, double y, double z)
	{
		float f = world.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
		EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, stack.copy());
		entityitem.motionX = world.rand.nextGaussian() * 0.05;
		entityitem.motionY = world.rand.nextGaussian() * 0.05 + 0.2;
		entityitem.motionZ = world.rand.nextGaussian() * 0.05;
		world.spawnEntityInWorld(entityitem);
	}
}
