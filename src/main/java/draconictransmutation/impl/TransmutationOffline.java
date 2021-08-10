package draconictransmutation.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import draconictransmutation.DTCore;
import draconictransmutation.api.ItemInfo;
import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.IKnowledgeProvider;
import draconictransmutation.impl.capability.KnowledgeImpl;
import draconictransmutation.utils.ItemHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TransmutationOffline {

	private static final IKnowledgeProvider NOT_FOUND_PROVIDER = immutableCopy(ProjectEAPI.KNOWLEDGE_CAPABILITY.getDefaultInstance());

	private static final Map<UUID, IKnowledgeProvider> cachedKnowledgeProviders = new HashMap<>();

	public static void cleanAll() {
		cachedKnowledgeProviders.clear();
	}

	public static void clear(UUID playerUUID) {
		cachedKnowledgeProviders.remove(playerUUID);
	}

	static IKnowledgeProvider forPlayer(UUID playerUUID) {
		if (!cachedKnowledgeProviders.containsKey(playerUUID)) {
			if (!cacheOfflineData(playerUUID)) {
				cachedKnowledgeProviders.put(playerUUID, NOT_FOUND_PROVIDER);
			}
		}

		return cachedKnowledgeProviders.get(playerUUID);
	}

	private static boolean cacheOfflineData(UUID playerUUID) {
		Preconditions.checkState(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER, "CRITICAL: Trying to read filesystem on client!!");
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		File playerData = server.func_240776_a_(FolderName.PLAYERDATA).toFile();
		if (playerData.exists()) {
			File player = new File(playerData, playerUUID.toString() + ".dat");
			if (player.exists() && player.isFile()) {
				try (FileInputStream in = new FileInputStream(player)) {
					CompoundNBT playerDat = CompressedStreamTools.readCompressed(in); // No need to create buffered stream, that call does it for us
					CompoundNBT knowledgeProvider = playerDat.getCompound("ForgeCaps").getCompound(KnowledgeImpl.Provider.NAME.toString());

					IKnowledgeProvider provider = ProjectEAPI.KNOWLEDGE_CAPABILITY.getDefaultInstance();
					ProjectEAPI.KNOWLEDGE_CAPABILITY.readNBT(provider, null, knowledgeProvider);
					cachedKnowledgeProviders.put(playerUUID, immutableCopy(provider));

					DTCore.debugLog("Caching offline data for UUID: {}", playerUUID);
					return true;
				} catch (IOException e) {
					DTCore.LOGGER.warn("Failed to cache offline data for API calls for UUID: {}", playerUUID);
				}
			}
		}

		return false;
	}

	private static IKnowledgeProvider immutableCopy(final IKnowledgeProvider toCopy) {
		return new IKnowledgeProvider() {
			final Set<ItemInfo> immutableKnowledge = ImmutableSet.copyOf(toCopy.getKnowledge());
			final IItemHandlerModifiable immutableInputLocks = ItemHelper.immutableCopy(toCopy.getInputAndLocks());

			@Override
			public boolean hasFullKnowledge() {
				return toCopy.hasFullKnowledge();
			}

			@Override
			public void setFullKnowledge(boolean fullKnowledge) {
			}

			@Override
			public void clearKnowledge() {
			}

			@Override
			public boolean hasKnowledge(@Nonnull ItemInfo info) {
				return toCopy.hasKnowledge(info);
			}

			@Override
			public boolean addKnowledge(@Nonnull ItemInfo info) {
				return false;
			}

			@Override
			public boolean removeKnowledge(@Nonnull ItemInfo info) {
				return false;
			}

			@Nonnull
			@Override
			public Set<ItemInfo> getKnowledge() {
				return immutableKnowledge;
			}

			@Nonnull
			@Override
			public IItemHandler getInputAndLocks() {
				return immutableInputLocks;
			}

			@Override
			public BigInteger getEmc() {
				return toCopy.getEmc();
			}

			@Override
			public void setEmc(BigInteger emc) {
			}

			@Override
			public void sync(@Nonnull ServerPlayerEntity player) {
				toCopy.sync(player);
			}

			@Override
			public void syncEmc(@Nonnull ServerPlayerEntity player) {
				toCopy.syncEmc(player);
			}

			@Override
			public void syncKnowledgeChange(@Nonnull ServerPlayerEntity player, ItemInfo change, boolean learned) {
				toCopy.syncKnowledgeChange(player, change, learned);
			}

			@Override
			public void syncInputAndLocks(@Nonnull ServerPlayerEntity player, List<Integer> slotsChanged, TargetUpdateType updateTargets) {
				toCopy.syncInputAndLocks(player, slotsChanged, updateTargets);
			}

			@Override
			public void receiveInputsAndLocks(Map<Integer, ItemStack> changes) {
			}

			@Override
			public CompoundNBT serializeNBT() {
				return toCopy.serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundNBT nbt) {
			}
		};
	}
}