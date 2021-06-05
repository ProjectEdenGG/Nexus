package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.clientside;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.Collector;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import me.pugabyte.nexus.models.bearfair21.ClientsideContentService;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityItemFrame;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ClientsideContentManager implements Listener {
	private static final ClientsideContentService contentService = new ClientsideContentService();
	private static final BearFair21UserService userService = new BearFair21UserService();
	//
	private static final HashMap<UUID, List<EntityItemFrame>> playerItemFrames = new HashMap<>();
	private static final List<NPCNameTag> playerNPCNameTags = new ArrayList<>();


	public ClientsideContentManager() {
		Nexus.registerListener(this);
		blockTask();
		npcTask();
	}

	public static void startup() {
		for (Player player : BearFair21.getPlayers())
			sendSpawnItemFrames(player);
	}

	public static void shutdown() {
		for (UUID uuid : playerItemFrames.keySet()) {
			Player player = getPlayer(uuid);
			if (player == null) continue;

			List<EntityItemFrame> itemFrames = playerItemFrames.get(uuid);
			for (EntityItemFrame itemFrame : itemFrames) {
				PacketUtils.entityDestroy(player, itemFrame);
			}
		}
		playerItemFrames.clear();

		for (NPCNameTag nameTag : playerNPCNameTags) {
			Player player = getPlayer(nameTag.getPlayerUuid());
			if (player == null) continue;

			List<EntityArmorStand> armorStands = nameTag.getArmorStands();
			for (EntityArmorStand armorStand : armorStands) {
				PacketUtils.entityDestroy(player, armorStand);
			}
		}
		playerNPCNameTags.clear();
	}

	private void blockTask() {
		Tasks.repeat(0, Time.TICK.x(10), () -> {
			Set<Player> players = BearFair21.getPlayers();
			for (Content content : contentService.getList()) {
				if (content.isItemFrame()) continue;

				for (Player player : players) {
					if (!isNear(player, content.getLocation(), 75)) continue;
					if (!canSee(player, content)) continue;

					player.sendBlockChange(content.getLocation(), content.getMaterial().createBlockData());
				}
			}
		});
	}

	private void npcTask() {
		Tasks.repeat(0, Time.SECOND.x(1), () -> {
			Set<Player> players = BearFair21.getPlayers();
			for (Player player : players) {
				for (BearFair21NPC bearFair21NPC : BearFair21NPC.values()) {
					NPC npc = bearFair21NPC.getNPC();
					if (npc == null) continue;

					if (!isNear(player, npc.getEntity().getLocation(), 8)) {
						if (canSee(player, bearFair21NPC))
							removeNPCNameTag(player, bearFair21NPC);
						continue;
					}

					if (!hasMet(player, bearFair21NPC)) continue;
					if (canSee(player, bearFair21NPC)) continue;

					List<EntityArmorStand> armorStands = bearFair21NPC.showName(player);
					NPCNameTag nameTag = new NPCNameTag(bearFair21NPC.getId(), player.getUniqueId(), armorStands);
					playerNPCNameTags.add(nameTag);
				}
			}
		});
	}

	private static boolean hasMet(Player player, BearFair21NPC npc) {
		return userService.get(player).getMetNPCs().contains(npc.getId());
	}

	private static boolean isNear(Player player, Location location, int distance) {
		return location.distance(player.getLocation()) < distance;
	}

	private static boolean canSee(Player player, Content content) {
		return userService.get(player).getClientsideLocations().contains(content.getLocation());
	}

	private static boolean canSee(Player player, BearFair21NPC npc) {
		return getNPCNameTag(player, npc.getId()) != null;
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		check(event.getPlayer());
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		check(event.getPlayer());
	}

	private void check(Player player) {
		Tasks.wait(1, () -> {
			if (!BearFair21.isAtBearFair(player))
				return;

			if (BearFair21.getPlayers().size() == 1)
				Collector.spawn();

			sendSpawnItemFrames(player);
		});
	}

	public static void sendSpawnItemFrames(Player player) {
		sendSpawnItemFrames(player, contentService.getList());
	}

	public static void sendSpawnItemFrames(Player player, List<Content> contentList) {
		for (Content content : contentList) {
			if (!content.isItemFrame()) continue;
			if (!canSee(player, content)) continue;

			EntityItemFrame itemFrame = PacketUtils.spawnItemFrame(player, content.getLocation(), content.getBlockFace(),
					content.getItemStack(), content.getRotation(), false, true);


			UUID uuid = player.getUniqueId();
			if (!playerItemFrames.containsKey(uuid))
				playerItemFrames.put(uuid, new ArrayList<>());

			playerItemFrames.get(uuid).add(itemFrame);
		}
	}

	private static Player getPlayer(UUID uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null) return null;
		return offlinePlayer.getPlayer();
	}

	private static void removeNPCNameTag(Player player, BearFair21NPC npc) {
		NPCNameTag nameTag = getNPCNameTag(player, npc.getId());
		if (nameTag == null) return;

		for (EntityArmorStand armorStand : nameTag.getArmorStands())
			PacketUtils.entityDestroy(player, armorStand);

		playerNPCNameTags.remove(nameTag);
	}

	public static NPCNameTag getNPCNameTag(Player player, int npcId) {
		UUID uuid = player.getUniqueId();
		for (NPCNameTag nameTag : playerNPCNameTags) {
			if (nameTag.getNpcId() == npcId && nameTag.getPlayerUuid().equals(uuid))
				return nameTag;
		}

		return null;
	}

	public static void addCategory(BearFair21User user, ContentCategory category) {
		BearFair21UserService userService = new BearFair21UserService();
		ClientsideContent clientsideContent = contentService.get0();
		List<Content> contentList = clientsideContent.getContentList();

		List<Location> locations = new ArrayList<>();
		List<Content> newContent = new ArrayList<>();
		for (Content content : contentList) {
			if (content.getCategory().equals(category)) {
				newContent.add(content);
				locations.add(content.getLocation());
			}
		}

		user.getClientsideLocations().addAll(locations);
		userService.save(user);

		ClientsideContentManager.sendSpawnItemFrames(user.getPlayer(), newContent);
	}
}
