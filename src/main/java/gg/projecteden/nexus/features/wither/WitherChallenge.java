package gg.projecteden.nexus.features.wither;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.HealCommand;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.features.wither.fights.CorruptedFight;
import gg.projecteden.nexus.features.wither.fights.EasyFight;
import gg.projecteden.nexus.features.wither.fights.HardFight;
import gg.projecteden.nexus.features.wither.fights.MediumFight;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.WorldGroup.isResourceWorld;

@NoArgsConstructor
public class WitherChallenge extends Feature implements Listener {

	public static final String PREFIX = StringUtils.getPrefix("Wither");
	public static final Location cageLoc = location(-151.5, 76, -69.5, 180, 0);
	public static WitherFight currentFight;
	public static List<UUID> queue = new ArrayList<>();
	public static boolean maintenance = false;

	static Location location(double x, double y, double z) {
		return location(x, y, z, 0, 0);
	}

	static Location location(double x, double y, double z, float yaw, float pitch) {
		return new Location(Bukkit.getWorld("events"), x, y, z, yaw, pitch);
	}

	@Getter
	private static final ItemStack witherFragment = new ItemBuilder(Material.GHAST_TEAR)
		.name("&eWither Fragment")
		.lore("&7Can be used to craft", "&7Wither Skeleton Skulls")
		.customModelData(1)
		.build();

	public static void reset() {
		reset(true);
	}

	public static void reset(boolean processQueue) {
		if (currentFight != null) {
			Nexus.unregisterListener(currentFight);
			currentFight.tasks.forEach(Tasks::cancel);
			if (currentFight.wither != null)
				currentFight.wither.remove();

			currentFight.scoreboardTeams.values().forEach(Team::unregister);
			currentFight.sendSpectatorsToSpawn();
			currentFight = null;
		}
		WorldGuardUtils worldGuardUtils = new WorldGuardUtils("events");
		ProtectedRegion region = worldGuardUtils.getProtectedRegion("witherarena");
		worldGuardUtils.getEntitiesInRegion(region.getId()).forEach(e -> {
			if (e.getType() != EntityType.PLAYER)
				e.remove();
		});
		new WorldEditUtils("events").paster().file("wither_arena").at(region.getMinimumPoint()).pasteAsync();
		if (!maintenance && processQueue)
			processQueue();
	}

	public static void processQueue() {
		if (queue.size() == 0)
			return;

		UUID nextPlayer = queue.get(0);
		if (PlayerUtils.getPlayer(nextPlayer).getPlayer() == null) {
			queue.remove(nextPlayer);
			processQueue();
		}
		PlayerUtils.send(PlayerUtils.getPlayer(nextPlayer).getPlayer(),
				new JsonBuilder(PREFIX + "It is now your time to fight the wither! &e&lClick here to select the difficulty")
						.command("/wither challenge")
						.hover("&eThis will open the difficulty selection menu"));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!new WorldGuardUtils(player.getWorld()).isInRegion(player.getLocation(), "witherarena"))
			return;

		if (currentFight == null) {
			Warps.spawn(player);
			HealCommand.healPlayer(player);
			return;
		}

		if (currentFight.party == null) {
			Warps.spawn(player);
			HealCommand.healPlayer(player);
			return;
		}

		if (currentFight.isInParty(player))
			return;

		Warps.spawn(player);
		HealCommand.healPlayer(player);
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event) {
		if (currentFight == null)
			return;

		final Player player = event.getPlayer();
		if (!currentFight.isInParty(player))
			return;

		currentFight.broadcastToParty("&e" + Nickname.of(player) + " &3has logged out. They have one minute to return before they are automatically removed from the party.");
		Tasks.wait(TickTime.MINUTE, () -> {
			if (currentFight == null)
				return;

			if (player.isOnline())
				return;

			currentFight.party.remove(player.getUniqueId());
			if (currentFight.alivePlayers != null)
				currentFight.alivePlayers.remove(player.getUniqueId());
			currentFight.broadcastToParty("&e" + Nickname.of(player) + " &ehas been removed from the party.");
		});
	}

	@EventHandler
	public void onTeleportIntoArena(PlayerTeleportEvent event) {
		if (!new WorldGuardUtils("events").isInRegion(event.getTo(), "witherarena"))
			return;

		final Player player = event.getPlayer();
		if (Rank.of(player).isStaff())
			return;

		if (currentFight == null) {
			cancelTeleport(event);
			return;
		}

		if (!currentFight.isAlive(player) && !currentFight.isSpectating(player))
			cancelTeleport(event);
	}

	@EventHandler
	public void onTeleportOutOfArena(PlayerTeleportEvent event) {
		if (!new WorldGuardUtils("events").isInRegion(event.getFrom(), "witherarena"))
			return;

		if (new WorldGuardUtils("events").isInRegion(event.getTo(), "witherarena"))
			return;

		if (currentFight == null)
			return;

		final Player player = event.getPlayer();
		if (currentFight.isSpectating(player)) {
			currentFight.getSpectators().remove(player.getUniqueId());
			player.setGameMode(GameMode.SURVIVAL);
			return;
		}

		if (!currentFight.isAlive(player))
			return;

		if (!currentFight.isStarted())
			return;

		event.setCancelled(true);
		PlayerUtils.send(player, PREFIX + "&cYou cannot teleport out of the wither arena during the fight. " +
				"Use &c/wither quit &3to resign from the fight");
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (!event.getEntity().getType().equals(EntityType.WITHER))
			return;

		World world = event.getLocation().getWorld();
		if (world.getName().equalsIgnoreCase("events") || isResourceWorld(world))
			return;

		if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherDeath(EntityDeathEvent event) {
		if (event.getEntityType() == EntityType.WITHER)
			event.getDrops().clear();
	}

	public void cancelTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled())
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), PREFIX + "&cYou cannot teleport into the wither arena if you are not an alive member of the current party");
	}

	@Getter
	public enum Difficulty {
		EASY(ChatColor.GREEN, EasyFight.class, Material.LIME_CONCRETE, "&712.5% chance of star drop"),
		MEDIUM(ChatColor.GOLD, MediumFight.class, Material.ORANGE_CONCRETE, "&725% chance of star drop", "&7If no star is dropped,", "&7you will receive 1 Wither Crate Key"),
		HARD(ChatColor.RED, HardFight.class, Material.RED_CONCRETE, "&750% chance of star drop", "&7If no star is dropped,", "&7you will receive 2 Wither Crate Keys"),
		CORRUPTED(ChatColor.DARK_GRAY, CorruptedFight.class, Material.BLACK_CONCRETE, "&7100% chance of star drop", "&7and 2 Wither Crate Keys", " ", "&cFull Netherite Armor Recommended");

		private final ChatColor color;
		private final Class<? extends WitherFight> witherFightClass;
		private final Material menuMaterial;
		private final List<String> description;

		Difficulty(ChatColor color, Class<? extends WitherFight> witherFightClass, Material menuMaterial, String... description) {
			this.color = color;
			this.witherFightClass = witherFightClass;
			this.menuMaterial = menuMaterial;
			this.description = Arrays.asList(description);
		}

		public String getTitle() {
			return StringUtils.colorize(color + "&l" + StringUtils.camelCase(name()));
		}
	}

}
