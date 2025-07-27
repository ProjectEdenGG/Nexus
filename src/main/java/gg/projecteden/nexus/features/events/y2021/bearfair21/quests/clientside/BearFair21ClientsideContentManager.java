package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.clientside;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideArmorStand;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21Collector;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import gg.projecteden.nexus.models.bearfair21.BearFair21User;
import gg.projecteden.nexus.models.bearfair21.BearFair21UserService;
import gg.projecteden.nexus.models.bearfair21.ClientsideContent.Content;
import gg.projecteden.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import gg.projecteden.nexus.models.bearfair21.ClientsideContentService;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BearFair21ClientsideContentManager implements Listener {
	private static final ClientsideContentService contentService = new ClientsideContentService();
	private static final BearFair21UserService userService = new BearFair21UserService();
	//
	@Getter
	private static final HashMap<UUID, List<Entity>> playerEntities = new HashMap<>();
	private static final List<BearFair21NPCNameTag> playerNPCNameTags = new ArrayList<>();

	public BearFair21ClientsideContentManager() {
		Tasks.async(() -> {
			Nexus.registerListener(this);
			blockTask();
			schematicTask();
			npcTask();
			startup();
		});
	}

	public static void startup() {
		for (Player player : BearFair21.getPlayers())
			sendSpawnContent(player);
	}

	public static void shutdown() {
		for (UUID uuid : playerEntities.keySet()) {
			Player player = getPlayer(uuid);
			if (player == null) continue;

			List<Entity> entities = playerEntities.get(uuid);
			for (Entity entity : entities) {
				PacketUtils.entityDestroy(player, entity.getId());
			}
		}
		playerEntities.clear();

		for (BearFair21NPCNameTag nameTag : playerNPCNameTags) {
			Player player = getPlayer(nameTag.getPlayerUuid());
			if (player == null) continue;

			List<ArmorStand> armorStands = nameTag.getArmorStands();
			for (ArmorStand armorStand : armorStands) {
				PacketUtils.entityDestroy(player, armorStand.getId());
			}
		}
		playerNPCNameTags.clear();
	}

	private void blockTask() {
		Tasks.repeat(0, TickTime.TICK.x(10), () -> {
			Set<Player> players = BearFair21.getPlayers();
			for (Content content : contentService.getList()) {
				if (!content.isBlock()) continue;

				for (Player player : players) {
					if (!isNear(player, content.getLocation(), 75)) continue;
					if (!canSee(player, content)) continue;

					final BlockData blockData = content.getMaterial().createBlockData();
					if (content.getBlockFace() != null)
						((Directional) blockData).setFacing(content.getBlockFace());
					player.sendBlockChange(content.getLocation(), blockData);
				}
			}
		});
	}

	private void schematicTask() {
		Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
			Set<Player> players = BearFair21.getPlayers();
			for (Content content : contentService.getList()) {
				if (!content.isSchematic()) continue;

				for (Player player : players) {
					if (!isNear(player, content.getLocation(), 75)) continue;
					if (!canSee(player, content)) continue;

					BearFair21.worldedit().paster()
						.file(content.getSchematic())
						.at(content.getLocation())
						.buildClientSide(player);
				}
			}
		});
	}

	private void npcTask() {
		Tasks.repeat(0, TickTime.SECOND.x(1), () -> {
			Set<Player> players = BearFair21.getPlayers();
			for (Player player : players) {
				for (BearFair21NPC bearFair21NPC : BearFair21NPC.values()) {
					NPC npc = bearFair21NPC.getNPC();
					if (npc == null) continue;

					if (!isNear(player, npc.getStoredLocation(), 8)) {
						if (canSee(player, bearFair21NPC))
							removeNPCNameTag(player, bearFair21NPC);
						continue;
					}

					if (!hasMet(player, bearFair21NPC)) continue;
					if (canSee(player, bearFair21NPC)) continue;

					List<ArmorStand> armorStands = bearFair21NPC.showName(player);
					BearFair21NPCNameTag nameTag = new BearFair21NPCNameTag(bearFair21NPC.getId(), player.getUniqueId(), armorStands);
					playerNPCNameTags.add(nameTag);
				}
			}
		});
	}

	private static boolean hasMet(Player player, BearFair21NPC npc) {
		return userService.get(player).getMetNPCs().contains(npc.getId());
	}

	private static boolean isNear(Player player, Location location, int distance) {
		return Distance.distance(location, player).lt(distance);
	}

	private static boolean canSee(Player player, Content content) {
		return userService.get(player).getContentCategories().contains(content.getCategory());
	}

	public static boolean canSee(Player player, ContentCategory category) {
		return userService.get(player).getContentCategories().contains(category);
	}

	private static boolean canSee(Player player, BearFair21NPC npc) {
		return getNPCNameTag(player, npc.getId()) != null;
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		check(event.getPlayer());
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		check(event.getPlayer());
	}

	private void check(Player player) {
		Tasks.wait(1, () -> {
			if (BearFair21.isNotAtBearFair(player))
				return;

			if (BearFair21.getPlayers().size() == 1)
				BearFair21Collector.spawn();

			sendSpawnContent(player);
		});
	}

	public static void sendRemoveContent(Player player, List<Content> contentList) {
		if (!playerEntities.containsKey(player.getUniqueId()))
			return;
		List<Entity> entities = new ArrayList<>(playerEntities.get(player.getUniqueId()));
		if (Nullables.isNullOrEmpty(entities))
			return;

		for (Content content : contentList) {
			entities.stream()
				.filter(entity -> LocationUtils.isFuzzyEqual(content.getLocation(), entity.getBukkitEntity().getLocation())).toList()
				.forEach(entity -> {
					PacketUtils.entityDestroy(player, entity.getId());
					playerEntities.get(player.getUniqueId()).remove(entity);
				});
		}
	}

	public static void sendRemoveEntityFrom(Player player, Location location, EntityType<?> entityType) {
		List<Entity> entities = new ArrayList<>(playerEntities.get(player.getUniqueId()));
		for (Entity entity : entities) {
			if (!entity.getType().equals(entityType))
				continue;

			if (LocationUtils.isFuzzyEqual(entity.getBukkitEntity().getLocation(), location)) {
				PacketUtils.entityDestroy(player, entity.getId());
				playerEntities.get(player.getUniqueId()).remove(entity);
			}
		}
	}

	// This spawns the entity, but the player cannot see the entity
	public static void sendSpawnSlime(Player player, Location location) {
		Slime slime = PacketUtils.spawnSlime(player, location, 2, false, true);
		addClientsideEntity(player, slime);
	}

	public static void sendSpawnArmorStand(Player player, Location location) {
		addClientsideEntity(player, ClientSideArmorStand.builder()
			.location(location)
			.invisible(true)
			.small(true)
			.glowing(true)
			.send(player)
			.entity());
	}

	public static void sendSpawnContent(Player player) {
		sendSpawnContent(player, contentService.getList(), false);
	}

	public static void sendSpawnContent(Player player, List<Content> contentList) {
		sendSpawnContent(player, contentList, false);
	}

	public static void sendSpawnContent(Player player, List<Content> contentList, boolean bypassChecks) {
		for (Content content : contentList) {
			if (!content.isItemFrame()) continue;
			if (!bypassChecks) {
				if (!canSee(player, content))
					continue;
			}

			addClientsideEntity(player, ClientSideItemFrame.builder()
					.location(content.getLocation())
					.blockFace(content.getBlockFace())
					.content(content.getItemStack())
					.rotation(content.getRotation())
					.invisible(true)
					.send(player)
					.entity());
		}
	}

	private static void addClientsideEntity(Player player, Entity entity) {
		playerEntities.computeIfAbsent(player.getUniqueId(), $ -> new ArrayList<>()).add(entity);
	}

	private static Player getPlayer(UUID uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null) return null;
		return offlinePlayer.getPlayer();
	}

	private static void removeNPCNameTag(Player player, BearFair21NPC npc) {
		BearFair21NPCNameTag nameTag = getNPCNameTag(player, npc.getId());
		if (nameTag == null) return;

		for (ArmorStand armorStand : nameTag.getArmorStands())
			PacketUtils.entityDestroy(player, armorStand.getId());

		playerNPCNameTags.remove(nameTag);
	}

	public static BearFair21NPCNameTag getNPCNameTag(Player player, int npcId) {
		UUID uuid = player.getUniqueId();
		for (BearFair21NPCNameTag nameTag : playerNPCNameTags) {
			if (nameTag.getNpcId() == npcId && nameTag.getPlayerUuid().equals(uuid))
				return nameTag;
		}

		return null;
	}

	public static void addCategory(BearFair21User user, ContentCategory category) {
		addCategory(user, category, 0);
	}

	public static void addCategory(BearFair21User user, ContentCategory category, long delay) {
		List<Content> contentList = contentService.get0().getContentList();

		List<Content> newContent = new ArrayList<>();
		for (Content content : contentList)
			if (content.getCategory().equals(category))
				newContent.add(content);

		new BearFair21UserService().edit(user, $ -> user.getContentCategories().add(category));

		final Runnable runnable = () -> BearFair21ClientsideContentManager.sendSpawnContent(user.getOnlinePlayer(), newContent);
		if (delay > 0)
			Tasks.wait(delay, runnable);
		else
			runnable.run();
	}

	public static void removeCategory(BearFair21User user, ContentCategory category) {
		removeCategory(user, category, 0);
	}

	public static void removeCategory(BearFair21User user, ContentCategory category, long delay) {
		List<Content> contentList = contentService.get0().getContentList();

		List<Content> oldContent = new ArrayList<>();
		for (Content content : contentList)
			if (content.getCategory().equals(category))
				oldContent.add(content);

		new BearFair21UserService().edit(user, $ -> user.getContentCategories().remove(category));

		final Runnable runnable = () -> BearFair21ClientsideContentManager.sendRemoveContent(user.getOnlinePlayer(), oldContent);
		if (delay > 0)
			Tasks.wait(delay, runnable);
		else
			runnable.run();
	}
}
