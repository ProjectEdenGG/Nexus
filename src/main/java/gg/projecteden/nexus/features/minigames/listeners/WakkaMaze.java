package gg.projecteden.nexus.features.minigames.listeners;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WakkaMaze implements Listener {
	private static final ItemStack exitKey = new ItemBuilder(Material.TRIPWIRE_HOOK).name("Exit Key").undroppable().build();
	protected static WorldEditUtils worldedit = Minigames.worldedit();

	private static Arena getArena() {
		return ArenaManager.get(WakkaMaze.class.getSimpleName());
	}

	public boolean isPlayingThis(Minigamer minigamer) {
		if (minigamer == null || minigamer.getMatch() == null)
			return false;

		if (!minigamer.getMatch().getArena().getName().equalsIgnoreCase(getClass().getSimpleName()))
			return false;

		return minigamer.isIn(getArena().getMechanic());
	}

	public boolean isPlayingThis(Match match) {
		if (match == null)
			return false;

		return match.getArena().getName().equalsIgnoreCase(getClass().getSimpleName());
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!isPlayingThis(minigamer))
			return;

		Match match = minigamer.getMatch();
		String regionId = event.getRegion().getId();

		if (regionId.equalsIgnoreCase(match.getArena().getProtectedRegion("exit").getId())) {
			if (!PlayerUtils.playerHas(player, exitKey))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!isPlayingThis(minigamer))
			return;

		Match match = minigamer.getMatch();
		String regionId = event.getRegion().getId();

		for (TeleporterType teleporter : TeleporterType.values()) {
			String teleportRegionId = match.getArena().getProtectedRegion(teleporter.getRegionId()).getId();
			if (teleportRegionId.equalsIgnoreCase(regionId)) {
				teleporter.teleport(player);
				return;
			}
		}
	}

	@EventHandler
	public void on(MatchStartEvent event) {
		if (!isPlayingThis(event.getMatch())) return;

		for (WallType wallType : WallType.values()) {
			wallType.toggle();
		}

		event.getMatch().getTasks().repeat(0, TickTime.MINUTE.x(1), () -> WallType.toggleWalls(event.getMatch()));
		event.getMatch().getTasks().repeat(0, 2, () -> {
			for (Player player : event.getMatch().getPlayers()) {
				if (PlayerUtils.playerHas(player, exitKey))
					continue;

				worldedit.getBlocks(event.getMatch().getArena().getProtectedRegion("exit"))
					.forEach(block -> player.sendBlockChange(block.getLocation(), Material.JUNGLE_LEAVES.createBlockData()));
			}
		});
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!isPlayingThis(minigamer))
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		ItemStack itemStack = itemFrame.getItem();
		if (Nullables.isNullOrAir(itemStack))
			return;

		if (!ItemUtils.isFuzzyMatch(exitKey, itemStack))
			return;

		if (PlayerUtils.playerHas(player, exitKey))
			return;

		minigamer.tell("The exit has been revealed!");
		PlayerUtils.giveItem(player, exitKey);

		worldedit.getBlocks(minigamer.getMatch().getArena().getProtectedRegion("exit"))
			.forEach(block -> player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData()));
	}

	private enum WallType {
		_1("1_1", "1_2"),
		_2("2_1", "2_2"),
		_3_1("3_1", "3_2"),
		;

		@Getter
		private final String region1;
		@Getter
		private final String region2;

		private int wallNum = 0;

		WallType(String region1, String region2) {
			this.region1 = "wall_" + region1;
			this.region2 = "wall_" + region2;
		}

		static void toggleWalls(Match match) {
			boolean changed = false;
			for (WallType wallType : WallType.values()) {
				if (RandomUtils.chanceOf(25)) {
					wallType.toggle();
					changed = true;
				}
			}

			if (changed) {
				for (Player player : match.getPlayers()) {
					ActionBarUtils.sendActionBar(player, "The maze has changed!");
				}
			}
		}

		private void toggle() {
			if (wallNum == 0) {
				wallNum = 1;
				worldedit.getBlocks(getArena().getRegion(region1)).forEach(block -> block.setType(Material.JUNGLE_LEAVES));
				worldedit.getBlocks(getArena().getRegion(region2)).forEach(block -> block.setType(Material.AIR));
			} else {
				wallNum = 0;
				worldedit.getBlocks(getArena().getRegion(region1)).forEach(block -> block.setType(Material.AIR));
				worldedit.getBlocks(getArena().getRegion(region2)).forEach(block -> block.setType(Material.JUNGLE_LEAVES));
			}
		}
	}

	private enum TeleporterType {
		YELLOW_1("yellow_1", new Location(Minigames.getWorld(), 1608.5, 8.5, 1237.5, -90, 0)),
		YELLOW_2("yellow_2", new Location(Minigames.getWorld(), 1628.5, 8.5, 1219.5, 90, 0)),
		ORANGE_1("orange_1", new Location(Minigames.getWorld(), 1573.5, 8.5, 1269.5, 0, 0)),
		ORANGE_2("orange_2", new Location(Minigames.getWorld(), 1631.5, 8.5, 1207.5, 90, 0)),
		BLUE_1("blue_1", new Location(Minigames.getWorld(), 1598.5, 8.5, 1237.5, 90, 0)),
		BLUE_2("blue_2", new Location(Minigames.getWorld(), 1575.5, 8.5, 1207.5, -90, 0)),
		;

		@Getter
		private final String regionId;
		@Getter
		private final Location location;

		TeleporterType(String regionId, Location location) {
			this.regionId = "teleporter_" + regionId;
			this.location = location;
		}

		private void teleport(Player player) {
			Minigamer minigamer = Minigamer.of(player);
			Location curLocation = player.getLocation().toCenterLocation().add(0, 1, 0);
			Location newLocation = this.getLocation().toCenterLocation();
			newLocation.setPitch(curLocation.getPitch());
			Vector velocity = player.getVelocity();

			new SoundBuilder(Sound.ENTITY_ENDERMAN_TELEPORT)
				.location(curLocation)
				.play();

			minigamer.teleportAsync(newLocation);
			player.setVelocity(velocity);
			newLocation.add(0, 1, 0);

			Tasks.wait(1, () -> {
				new SoundBuilder(Sound.ENTITY_ENDERMAN_TELEPORT)
					.location(newLocation)
					.play();
			});
		}
	}
}
