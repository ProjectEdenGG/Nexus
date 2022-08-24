package gg.projecteden.nexus.features.fakenpc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.fakenpc.FakeNPC.SkinProperties;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FakeNPCManager {
	@Getter
	private static final Set<FakeNPC> fakeNpcs = new HashSet<>();
	@Getter
	private static final Map<UUID, Set<FakeNPC>> playerFakeNPCs = new HashMap<>();
	private static final Map<UUID, FakeNPC> selected = new HashMap<>();

	public FakeNPCManager() {
		Tasks.repeat(0, TickTime.SECOND.x(1), () -> {
			Collection<? extends Player> players = OnlinePlayers.getAll();
			fakeNpcs.forEach(fakeNPC -> players.stream().filter(player -> isNear(player, fakeNPC)).toList().forEach(player -> {
				UUID playerUUID = player.getUniqueId();
				boolean visible = fakeNPC.isVisible();
				boolean isNear = isNear(player, fakeNPC);
				boolean canSee = canSee(playerUUID, fakeNPC);

				if (visible) {
					if (!canSee && isNear) {
						playerFakeNPCs.get(playerUUID).add(fakeNPC);
						FakeNPCPacketUtils.spawnFor(fakeNPC, player);
					} else if (canSee && !isNear) {
						playerFakeNPCs.get(playerUUID).remove(fakeNPC);
						FakeNPCPacketUtils.despawnFor(fakeNPC, player);
					}
				} else if (canSee) {
					playerFakeNPCs.get(playerUUID).remove(fakeNPC);
					FakeNPCPacketUtils.despawnFor(fakeNPC, playerUUID);
				}
			}));
		});
	}

	private static boolean canSee(UUID uuid, FakeNPC fakeNPC) {
		playerFakeNPCs.putIfAbsent(uuid, new HashSet<>());
		return playerFakeNPCs.get(uuid).contains(fakeNPC);
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

		fakeNPC.setEntityPlayer(FakeNPCNMSUtils.createEntityPlayer(UUID.randomUUID(), fakeNPC.getLocation(), fakeNPC.getName()));
		setSkin(fakeNPC, player);

		fakeNpcs.add(fakeNPC);
		return fakeNPC;
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
				FakeNPCPacketUtils.despawnFor(fakeNPC, uuid);
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
		FakeNPCNMSUtils.setLocation(fakeNPC.getEntityPlayer(), fakeNPC.getLocation());
		respawn(fakeNPC);
	}

	public static void setSkin(FakeNPC fakeNPC, Nerd nerd) {
		if (nerd.isOnline())
			setSkin(fakeNPC, nerd.getPlayer());
		else
			setSkin(fakeNPC, nerd.getOfflinePlayer());
	}

	public static void setSkin(FakeNPC fakeNPC, Player player) {
		Pair<String, String> skinData = FakeNPCUtils.getSkinData(player);
		setSkin(fakeNPC, player.getUniqueId().toString(), skinData.getFirst(), skinData.getSecond());
	}

	public static void setSkin(FakeNPC fakeNPC, OfflinePlayer player) {
		setSkinFromUUID(fakeNPC, player.getUniqueId().toString());
	}

	public static void setSkin(FakeNPC fakeNPC, String name) {
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader = new InputStreamReader(url.openStream());
			setSkinFromUUID(fakeNPC, new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString());
		} catch (Exception ex) {
			Nexus.warn("An error occurred when setting skin (from name) of FakeNPC: " + fakeNPC.getName());
			ex.printStackTrace();
		}
	}

	private static void setSkinFromUUID(FakeNPC fakeNPC, String uuid) {
		try {
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader = new InputStreamReader(url.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
			String texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();
			setSkin(fakeNPC, uuid, texture, signature);
		} catch (Exception ex) {
			Nexus.warn("An error occurred when setting skin (from UUID) of FakeNPC: " + fakeNPC.getName());
			ex.printStackTrace();
		}
	}

	private static void setSkin(FakeNPC fakeNPC, String uuid, String texture, String signature) {
		fakeNPC.setSkinProperties(new SkinProperties(uuid, texture, signature));
		fakeNPC.applySkin();
		Tasks.wait(1, () -> FakeNPCManager.respawn(fakeNPC));

	}

	public static void setMineSkin(FakeNPC fakeNPC, String url) {
		FakeNPCUtils.setMineSkin(fakeNPC, url, true).thenRun(() -> Tasks.wait(1, () -> respawn(fakeNPC)));
	}
}
