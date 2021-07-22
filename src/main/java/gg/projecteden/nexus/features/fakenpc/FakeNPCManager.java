package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.Getter;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakeNPCManager {
	@Getter
	private static final Set<FakeNPC> fakeNpcs = new HashSet<>();
	@Getter
	private static final Map<UUID, Set<FakeNPC>> playerFakeNPCs = new HashMap<>();
	private static final Map<UUID, FakeNPC> selected = new HashMap<>();

	public FakeNPCManager() {
		Tasks.repeat(0, Time.SECOND.x(1), () -> {
			Collection<? extends Player> players = PlayerUtils.getOnlinePlayers();
			fakeNpcs.forEach(fakeNPC -> {
				if (fakeNPC.isVisible()) {
					List<Player> nearby = players.stream().filter(player -> isNear(player, fakeNPC)).collect(Collectors.toList());
					nearby.forEach(player -> {
						UUID uuid = player.getUniqueId();
						boolean isNear = isNear(player, fakeNPC);
						boolean canSee = canSee(uuid, fakeNPC);

						if (!canSee && isNear) {
							playerFakeNPCs.get(uuid).add(fakeNPC);
							FakeNPCPacketUtils.spawnFakeNPC(player, fakeNPC);
						} else if (canSee && !isNear) {
							playerFakeNPCs.get(uuid).remove(fakeNPC);
							FakeNPCPacketUtils.despawnFakeNPC(player, fakeNPC);
						}
					});
				}
			});
		});
	}

	private static boolean canSee(UUID uuid, FakeNPC fakeNPC) {
		playerFakeNPCs.putIfAbsent(uuid, new HashSet<>());
		Set<FakeNPC> NPCs = playerFakeNPCs.get(uuid);
		return NPCs.contains(fakeNPC);
	}

	private boolean isNear(Player player, FakeNPC fakeNPC) {
		return player.getWorld().equals(fakeNPC.getLocation().getWorld());
	}

	public static void setSelected(Player player, FakeNPC fakeNPC) {
		selected.put(player.getUniqueId(), fakeNPC);
	}

	public static FakeNPC getSelected(Player player) {
		return selected.getOrDefault(player.getUniqueId(), null);
	}

	public static FakeNPC from(UUID uuid) {
		return fakeNpcs.stream().filter(fakeNPC -> fakeNPC.getUuid().equals(uuid)).findFirst().orElse(null);
	}

	public static FakeNPC createFakeNPC(Player player) {
		FakeNPC fakeNPC = new FakeNPC(player.getLocation(), Name.of(player));

		EntityPlayer entityPlayer = NMSUtils.createEntityPlayer(UUID.randomUUID(), fakeNPC.getLocation(), fakeNPC.getName());
		fakeNPC.setEntityPlayer(entityPlayer);
		FakeNPCUtils.setDefaultSkin(fakeNPC, player);

		fakeNpcs.add(fakeNPC);
		return fakeNPC;
	}

	public static void createHologram(FakeNPC fakeNPC, List<String> lines) {
		List<EntityArmorStand> armorStands = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++)
			armorStands.add(NMSUtils.createHologram(fakeNPC.getEntityPlayer().getWorld()));

		fakeNPC.getHologram().setArmorStandList(armorStands);
	}

	public static void spawn(FakeNPC fakeNPC) {
		fakeNPC.setVisible(true);
		fakeNPC.applySkin();
	}

	public static void despawn(FakeNPC fakeNPC) {
		fakeNPC.setVisible(false);
		playerFakeNPCs.keySet().forEach(uuid -> {
			if (canSee(uuid, fakeNPC)) {
				playerFakeNPCs.get(uuid).remove(fakeNPC);
				FakeNPCPacketUtils.despawnHologram(uuid, fakeNPC.getHologram());
				FakeNPCPacketUtils.despawnFakeNPC(uuid, fakeNPC);
			}
		});
	}

	public static void respawn(FakeNPC fakeNPC) {
		despawn(fakeNPC);
		spawn(fakeNPC);
	}

	public static void delete(FakeNPC fakeNPC) {
		despawn(fakeNPC);
		fakeNpcs.remove(fakeNPC);
	}

	public static void teleport(FakeNPC fakeNPC, Location location) {
		fakeNPC.setLocation(location);
		NMSUtils.setLocation(fakeNPC.getEntityPlayer(), fakeNPC.getLocation());
		respawn(fakeNPC);
	}

	public static void setMineSkin(FakeNPC fakeNPC, String url) {
		FakeNPCUtils.setMineSkin(fakeNPC, url, true).thenRun(() -> respawn(fakeNPC));
	}
}
