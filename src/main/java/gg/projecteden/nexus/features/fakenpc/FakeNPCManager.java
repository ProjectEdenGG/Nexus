package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram;
import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.features.fakenpc.types.PlayerNPC;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FakeNPCManager {
	@Getter
	private static final Set<FakeNPC> NPCList = new HashSet<>();
	@Getter
	private static final Map<UUID, Set<FakeNPC>> playerVisibleNPCs = new HashMap<>();
	@Getter
	private static final Map<UUID, Set<FakeNPC>> playerVisibleHolograms = new HashMap<>();
	private static final Map<UUID, FakeNPC> selected = new HashMap<>();

	public FakeNPCManager() {
		Tasks.repeat(0, TickTime.SECOND.x(1), () -> {
			Collection<? extends Player> players = OnlinePlayers.getAll();
			NPCList.forEach(fakeNPC -> players.forEach(player -> {
				UUID playerUUID = player.getUniqueId();

				// NPC
				boolean npcVisible = fakeNPC.isSpawned();
				boolean isNear = FakeNPCUtils.isInSameWorld(player, fakeNPC);
				boolean playerCanSeeNPC = FakeNPCUtils.isNPCVisibleFor(fakeNPC, playerUUID);

				if (npcVisible) {
					if (!playerCanSeeNPC && isNear) {
						playerVisibleNPCs.get(playerUUID).add(fakeNPC);
						FakeNPCPacketUtils.spawnFor(fakeNPC, player);
					} else if (playerCanSeeNPC && !isNear) {
						playerVisibleNPCs.get(playerUUID).remove(fakeNPC);
						FakeNPCPacketUtils.despawnFor(fakeNPC, player);
					}
				} else if (playerCanSeeNPC) {
					playerVisibleNPCs.get(playerUUID).remove(fakeNPC);
					FakeNPCPacketUtils.despawnFor(fakeNPC, player);
				}

				// HOLOGRAM
				Hologram hologram = fakeNPC.getHologram();
				boolean hologramVisible = hologram.isSpawned();
				VisibilityType visibilityType = hologram.getVisibilityType();
				boolean typeApplies = visibilityType.applies(fakeNPC, player);
				boolean playerCanSeeHologram = FakeNPCUtils.isHologramVisibleFor(fakeNPC, playerUUID);

				if (hologramVisible) {
					if (!playerCanSeeHologram && typeApplies) {
						playerVisibleHolograms.get(playerUUID).add(fakeNPC);
						FakeNPCPacketUtils.spawnHologramFor(fakeNPC, player);
					} else if (playerCanSeeHologram && !typeApplies) {
						playerVisibleHolograms.get(playerUUID).remove(fakeNPC);
						FakeNPCPacketUtils.despawnHologramFor(hologram, player);
					}
				} else if (playerCanSeeHologram) {
					getPlayerVisibleHolograms().get(playerUUID).remove(fakeNPC);
					FakeNPCPacketUtils.despawnHologramFor(hologram, player);
				}
			}));
		});

		// Look Close
		Tasks.repeat(0, TickTime.TICK, () -> {
			playerVisibleNPCs.keySet().forEach(playerUUID -> {
				OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(playerUUID);
				if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null)
					return;

				Player player = offlinePlayer.getPlayer();
				playerVisibleNPCs.get(playerUUID).forEach(fakeNPC -> {
					if (!fakeNPC.isLookClose())
						return;

					boolean playerCanSeeNPC = FakeNPCUtils.isNPCVisibleFor(fakeNPC, playerUUID);
					if (!playerCanSeeNPC)
						return;

					if (!fakeNPC.canSee(player))
						return;

					FakeNPCPacketUtils.lookAt(fakeNPC, player);
				});
			});
		});
	}

	public static void setSelected(Player player, FakeNPC fakeNPC) {
		selected.put(player.getUniqueId(), fakeNPC);
	}

	public static FakeNPC getSelected(Player player) {
		return selected.getOrDefault(player.getUniqueId(), null);
	}

	public static FakeNPC from(UUID uuid) {
		return NPCList.stream().filter(fakeNPC -> fakeNPC.getUuid().equals(uuid)).findFirst().orElse(null);
	}

	public static FakeNPC create(FakeNPCType type, Player owner) {
		// clean up
		FakeNPC fakeNPC = new FakeNPC(type, owner);
		if (type == FakeNPCType.PLAYER) {
			fakeNPC = new PlayerNPC(type, owner);
		}

		NPCList.add(fakeNPC);
		return fakeNPC;
	}
}
