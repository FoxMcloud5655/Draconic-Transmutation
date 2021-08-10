package draconictransmutation.impl.capability;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import draconictransmutation.DTCore;
import draconictransmutation.api.ItemInfo;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.IKnowledgeProvider;
import draconictransmutation.api.event.PlayerKnowledgeChangeEvent;
import draconictransmutation.capability.managing.SerializableCapabilityResolver;
import draconictransmutation.emc.EMCMappingHandler;
import draconictransmutation.emc.nbt.NBTManager;
import draconictransmutation.network.PacketHandler;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncChangePKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncEmcPKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncInputsAndLocksPKT;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeSyncPKT;
import draconictransmutation.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public final class KnowledgeImpl {

	public static void init() {
		CapabilityManager.INSTANCE.register(IKnowledgeProvider.class, new Capability.IStorage<IKnowledgeProvider>() {
			@Override
			public CompoundNBT writeNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, Direction side, INBT nbt) {
				if (nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
		}, () -> new DefaultImpl(null));
	}

	private static class DefaultImpl implements IKnowledgeProvider {

		@Nullable
		private final PlayerEntity player;
		private final Set<ItemInfo> knowledge = new HashSet<>();
		private final IItemHandlerModifiable inputLocks = new ItemStackHandler(9);
		private BigInteger emc = BigInteger.ZERO;
		private boolean fullKnowledge = false;

		private DefaultImpl(@Nullable PlayerEntity player) {
			this.player = player;
		}

		private void fireChangedEvent() {
			if (player != null && !player.world.isRemote) {
				MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
			}
		}

		@Override
		public boolean hasFullKnowledge() {
			return fullKnowledge;
		}

		@Override
		public void setFullKnowledge(boolean fullKnowledge) {
			boolean changed = this.fullKnowledge != fullKnowledge;
			this.fullKnowledge = fullKnowledge;
			if (changed) {
				fireChangedEvent();
			}
		}

		@Override
		public void clearKnowledge() {
			boolean hasKnowledge = fullKnowledge || !knowledge.isEmpty();
			knowledge.clear();
			fullKnowledge = false;
			if (hasKnowledge) {
				//If we previously had any knowledge fire the fact that our knowledge changed
				fireChangedEvent();
			}
		}

		@Nullable
		private ItemInfo getIfPersistent(@Nonnull ItemInfo info) {
			if (!info.hasNBT() || EMCMappingHandler.hasEmcValue(info)) {
				//If we have no NBT or the base mapping has an emc value for our item with the given NBT
				// then we don't have an extended state
				return null;
			}
			ItemInfo cleanedInfo = NBTManager.getPersistentInfo(info);
			if (cleanedInfo.hasNBT() && !EMCMappingHandler.hasEmcValue(cleanedInfo)) {
				//If we still have NBT after unimportant parts being stripped and it doesn't
				// directly have an EMC value, then we it has some persistent information
				return cleanedInfo;
			}
			return null;
		}

		@Override
		public boolean hasKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				//If we have all knowledge, check if the item has extra data and
				// may not actually be in our knowledge set but can be added to it
				ItemInfo persistentInfo = getIfPersistent(info);
				return persistentInfo == null || knowledge.contains(persistentInfo);
			}
			return knowledge.contains(NBTManager.getPersistentInfo(info));
		}

		@Override
		public boolean addKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				ItemInfo persistentInfo = getIfPersistent(info);
				if (persistentInfo == null) {
					//If the item doesn't have extra data, and we have all knowledge, don't actually add any
					return false;
				}
				//If it does have extra data, pretend we don't have full knowledge and try adding it as what we have is persistent.
				return tryAdd(persistentInfo);
			}
			return tryAdd(NBTManager.getPersistentInfo(info));
		}

		private boolean tryAdd(@Nonnull ItemInfo cleanedInfo) {
			if (knowledge.add(cleanedInfo)) {
				fireChangedEvent();
				return true;
			}
			return false;
		}

		@Override
		public boolean removeKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				//Check if we have any persistent information, and if so try removing that
				// as we may have it known as an "extra" item
				ItemInfo persistentInfo = getIfPersistent(info);
				return persistentInfo != null && tryRemove(persistentInfo);
			}
			return tryRemove(NBTManager.getPersistentInfo(info));
		}

		private boolean tryRemove(@Nonnull ItemInfo cleanedInfo) {
			if (knowledge.remove(cleanedInfo)) {
				fireChangedEvent();
				return true;
			}
			return false;
		}

		@Nonnull
		@Override
		public Set<ItemInfo> getKnowledge() {
			if (fullKnowledge) {
				Set<ItemInfo> allKnowledge = EMCMappingHandler.getMappedItems();
				//Make sure we include any extra items they have learned such as various enchanted items.
				allKnowledge.addAll(knowledge);
				return Collections.unmodifiableSet(allKnowledge);
			}
			return Collections.unmodifiableSet(knowledge);
		}

		@Nonnull
		@Override
		public IItemHandlerModifiable getInputAndLocks() {
			return inputLocks;
		}

		@Override
		public BigInteger getEmc() {
			return emc;
		}

		@Override
		public void setEmc(BigInteger emc) {
			this.emc = emc;
		}

		@Override
		public void sync(@Nonnull ServerPlayerEntity player) {
			PacketHandler.sendTo(new KnowledgeSyncPKT(serializeNBT()), player);
		}

		@Override
		public void syncEmc(@Nonnull ServerPlayerEntity player) {
			PacketHandler.sendTo(new KnowledgeSyncEmcPKT(getEmc()), player);
		}

		@Override
		public void syncKnowledgeChange(@Nonnull ServerPlayerEntity player, ItemInfo change, boolean learned) {
			PacketHandler.sendTo(new KnowledgeSyncChangePKT(change, learned), player);
		}

		@Override
		public void syncInputAndLocks(@Nonnull ServerPlayerEntity player, List<Integer> slotsChanged, TargetUpdateType updateTargets) {
			if (!slotsChanged.isEmpty()) {
				int slots = inputLocks.getSlots();
				Map<Integer, ItemStack> stacksToSync = new HashMap<>();
				for (int slot : slotsChanged) {
					if (slot >= 0 && slot < slots) {
						//Validate the slot is a valid index
						stacksToSync.put(slot, inputLocks.getStackInSlot(slot));
					}
				}
				if (!stacksToSync.isEmpty()) {
					//Validate it is not empty in case we were fed bad indices
					PacketHandler.sendTo(new KnowledgeSyncInputsAndLocksPKT(stacksToSync, updateTargets), player);
				}
			}
		}

		@Override
		public void receiveInputsAndLocks(Map<Integer, ItemStack> changes) {
			int slots = inputLocks.getSlots();
			for (Map.Entry<Integer, ItemStack> entry : changes.entrySet()) {
				int slot = entry.getKey();
				if (slot >= 0 && slot < slots) {
					//Validate the slot is a valid index
					inputLocks.setStackInSlot(slot, entry.getValue());
				}
			}
		}

		@Override
		public CompoundNBT serializeNBT() {
			CompoundNBT properties = new CompoundNBT();
			properties.putString("transmutationEmc", emc.toString());

			ListNBT knowledgeWrite = new ListNBT();
			for (ItemInfo i : knowledge) {
				knowledgeWrite.add(i.write(new CompoundNBT()));
			}

			properties.put("knowledge", knowledgeWrite);
			INBT lock = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inputLocks, null);
			if (lock != null) {
				properties.put("inputlock", lock);
			}
			properties.putBoolean("fullknowledge", fullKnowledge);
			return properties;
		}

		@Override
		public void deserializeNBT(CompoundNBT properties) {
			String transmutationEmc = properties.getString("transmutationEmc");
			emc = transmutationEmc.isEmpty() ? BigInteger.ZERO : new BigInteger(transmutationEmc);

			ListNBT list = properties.getList("knowledge", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				ItemInfo info = ItemInfo.read(list.getCompound(i));
				if (info != null) {
					knowledge.add(info);
				}
			}

			pruneStaleKnowledge();

			for (int i = 0; i < inputLocks.getSlots(); i++) {
				inputLocks.setStackInSlot(i, ItemStack.EMPTY);
			}

			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inputLocks, null, properties.getList("inputlock", Constants.NBT.TAG_COMPOUND));
			fullKnowledge = properties.getBoolean("fullknowledge");
		}

		private void pruneStaleKnowledge() {
			List<ItemInfo> toRemove = new ArrayList<>();
			List<ItemInfo> toAdd = new ArrayList<>();
			for (ItemInfo info : knowledge) {
				ItemInfo persistentInfo = NBTManager.getPersistentInfo(info);
				if (!info.equals(persistentInfo)) {
					//If something about the persistence changed and the item we have is no longer directly learnable
					// we remove it from our knowledge
					toRemove.add(info);
					//If the new persistent variant has an EMC value though we add it because that is what they would have learned
					// had they tried to consume the item now instead of before
					if (EMCHelper.doesItemHaveEmc(persistentInfo)) {
						toAdd.add(persistentInfo);
					}
				} else if (!EMCHelper.doesItemHaveEmc(info)) {
					//If the items do match but it just no longer has an EMC value, then we remove it as well
					toRemove.add(info);
				}
			}
			knowledge.removeAll(toRemove);
			knowledge.addAll(toAdd);
		}
	}

	public static class Provider extends SerializableCapabilityResolver<IKnowledgeProvider> {

		public static final ResourceLocation NAME = DTCore.rl("knowledge");

		public Provider(PlayerEntity player) {
			super(new DefaultImpl(player));
		}

		@Nonnull
		@Override
		public Capability<IKnowledgeProvider> getMatchingCapability() {
			return ProjectEAPI.KNOWLEDGE_CAPABILITY;
		}
	}

	private KnowledgeImpl() {
	}
}