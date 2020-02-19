package me.pugabyte.bncore.features.minigames.mechanics;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.ArcheryArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.ArcheryMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: On Player Quit --> if the game is active, unload their range

public class Archery extends TeamlessMechanic {
	WorldGuardUtils WGUtils = Minigames.getWorldGuardUtils();
	WorldEditUtils WEUtils = Minigames.getWorldEditUtils();

	@Override
	public String getName() {
		return "Archery";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.BOW);
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	@Override
	public void onInitialize(MatchInitializeEvent event) {
		super.onInitialize(event);
		clearRanges(event.getMatch());
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);
		Match match = event.getMatch();
		ArcheryMatchData matchData = match.getMatchData();

		// Load the next range locations
		int range = match.getMinigamers().size();
		matchData.loadRangeLocations(range, match);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		ArcheryMatchData matchData = match.getMatchData();

		match.getTasks().wait(20, () -> {
			matchData.removeInactiveRanges(match);
			for (Minigamer minigamer : match.getMinigamers()) {
				minigamer.scored(5);
			}

		});

		startTargetTask(match);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		clearRanges(event.getMatch());
	}

	// TODO: Needs a setting to show individual scoreboards
	@Override
	public Map<String, Integer> getScoreboardLines(Match match) {
		Map<String, Integer> lines = new HashMap<>();
		ArcheryMatchData matchData = match.getMatchData();

		if (match.isStarted()) {
			int timeLeft = match.getTimer().getTime();
			List<Minigamer> minigamers = match.getMinigamers();

			for (Minigamer minigamer : minigamers) {
				lines.put("&1&fScore: &c" + minigamer.getScore(), 0);
				lines.put("&2&fTargets Hit: &c" + matchData.getTargetsHit(minigamer), 0);
				lines.put("&3", 0);
				lines.put("&4&fTime Left: &c&l" + timeLeft, 0);
				lines.put("&5", 0);

				// TODO: Leaderboard (Needs to be sorted by score)
				for (Minigamer sbMinigamer : minigamers) {
					if (sbMinigamer.getScore() <= 0)
						continue;

					String name = sbMinigamer.getName();

					if (minigamer.equals(sbMinigamer))
						name = "&f&lYOU";

					int score = sbMinigamer.getScore();
					lines.put("&6&f" + name + "&f: &c" + score, 0);
				}
			}
		} else {
			for (Minigamer minigamer : match.getMinigamers())
				lines.put(minigamer.getName(), 0);
		}

		return lines;
	}

	public void startTargetTask(Match match) {
		ArcheryMatchData matchData = match.getMatchData();
		Map<ProtectedRegion, ArrayList<Location>> powderLocations = matchData.getPowderLocations();
		saveTargetSchems(match);
		Map<String, Schematic> targetSchems = matchData.getTargetSchematics();

		match.getTasks().repeat(0, 7 * 20, () -> {
			for (ProtectedRegion colorRegion : powderLocations.keySet()) {
				ArrayList<Location> locations = powderLocations.get(colorRegion);

				for (int i = 0; i < 10; i++) {
					Location randomXZ = Utils.getRandomElement(locations);
					int min = colorRegion.getMinimumPoint().getBlockY() + 2;
					int max = colorRegion.getMaximumPoint().getBlockY() - 2;
					int y = Utils.randomInt(min, max);

					Location targetLoc = randomXZ.getBlock().getLocation();
					targetLoc.setY(y);
					targetLoc = targetLoc.getBlock().getRelative(-1, 1, 0).getLocation();

					int range = matchData.getRangeNumber(colorRegion);
					Direction direction = matchData.getRangeDirection(range, match);
					if (direction == null)
						break;
					String color = matchData.getRangeColor(colorRegion);

					if (color.equalsIgnoreCase("red")) {
						if (direction.equals(Direction.NORTH))
							targetLoc = targetLoc.getBlock().getRelative(0, 0, -1).getLocation();
						else
							targetLoc = targetLoc.getBlock().getRelative(0, 0, 1).getLocation();
					} else if (color.equalsIgnoreCase("yellow")) {
						if (direction.equals(Direction.NORTH))
							targetLoc = targetLoc.getBlock().getRelative(0, 0, 1).getLocation();
						else
							targetLoc = targetLoc.getBlock().getRelative(0, 0, -1).getLocation();
					}

					if (canPlaceTarget(targetLoc)) {
						String key = (direction.name() + "_" + color).toLowerCase();
						Schematic schem = targetSchems.get(key);
						WEUtils.paste(schem, targetLoc);
						break;
					}
				}
			}
		});
	}

	public void saveTargetSchems(Match match) {
		ArcheryMatchData matchData = match.getMatchData();
		ArcheryArena arena = match.getArena();
		Set<ProtectedRegion> targetRegions = arena.getTargetRegions();

		targetRegions.forEach(targetRegion -> {
			Schematic schem = WEUtils.copy(WGUtils.convert(targetRegion));
			Direction direction = matchData.getTargetDirection(targetRegion);
			String color = matchData.getTargetColor(targetRegion);
			String key = (direction.name() + "_" + color).toLowerCase();
			matchData.getTargetSchematics().put(key, schem);
		});
	}

	public boolean canPlaceTarget(Location location) {
		List<Block> nearbyBlocks = Utils.getBlocksInRadius(location, 3);
		for (Block block : nearbyBlocks) {
			if (block.getType().equals(Material.CONCRETE) || block.getType().equals(Material.STONE_BUTTON))
				return false;
		}
		return true;
	}

	public void clearRanges(Match match) {
		ArcheryArena arena = match.getArena();
		for (int i = 1; i <= 10; i++) {
			Set<ProtectedRegion> rangeRegions = arena.getRegionsLike("range_[0-9]+_.*");
			rangeRegions.forEach(region -> {
				CuboidRegion expandedRegion = (CuboidRegion) WEUtils.expandAll(WGUtils.convert(region), 2);
				List<Block> blocks = WEUtils.getBlocks(expandedRegion);
				blocks.forEach(block -> {
					if (block.getType().equals(Material.CONCRETE) && block.getData() == 0)
						removeTarget(block);
				});
			});
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Arrow))
			return;

		if (!WGUtils.getRegionNamesAt(projectile.getLocation()).contains("archery"))
			return;

		Block hitBlock = event.getHitBlock();
		if (hitBlock == null)
			return;

		if (!(hitBlock.getType().equals(Material.CONCRETE) && hitBlock.getData() == 0))
			return;

		if (!(projectile.getShooter() instanceof Player))
			return;

		Player player = (Player) projectile.getShooter();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this))
			return;

		String color = ColorType.fromDurability(hitBlock.getRelative(0, 1, 0).getData()).getName();
		minigamer.scored(getPoints(color));

		projectile.remove();
		removeTarget(hitBlock);
	}

	public int getPoints(String color) {
		switch (color) {
			case "yellow":
				return 1;
			case "orange":
				return 3;
			case "red":
				return 5;
		}
		return 0;
	}

	public void removeTarget(Block target) {
		Collection<Entity> entities = target.getWorld().getNearbyEntities(target.getLocation(), 2, 2, 2);
		for (Entity entity : entities) {
			if (entity.getType().equals(EntityType.ARROW))
				entity.remove();
		}

		List<Block> blocks = Utils.getBlocksInRadius(target, 2);
		for (Block block : blocks) {
			if (block.getType().equals(Material.STONE_BUTTON))
				block.setType(Material.AIR);
		}

		for (Block block : blocks) {
			if (block.getType().equals(Material.CONCRETE))
				block.setType(Material.AIR);
		}
	}
}
