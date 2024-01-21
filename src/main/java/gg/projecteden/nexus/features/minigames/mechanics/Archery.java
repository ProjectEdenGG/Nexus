package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import gg.projecteden.nexus.features.minigames.models.arenas.ArcheryArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.ArcheryMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Scoreboard(sidebarType = MinigameScoreboard.Type.MINIGAMER)
public class Archery extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "Archery";
	}

	@Override
	public @NotNull String getDescription() {
		return "Shoot the targets and score extra points for the farthest targets";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.BOW);
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);
		clearRanges(event.getMatch());
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Match match = event.getMatch();
		ArcheryMatchData matchData = match.getMatchData();

		// Load the next range locations
		int range = match.getMinigamers().size();
		matchData.loadRangeLocations(range, match);
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);
		Match match = event.getMatch();
		ArcheryMatchData matchData = match.getMatchData();

		if (match.isStarted())
			matchData.removeInactiveRanges(match);
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		ArcheryMatchData matchData = match.getMatchData();

		match.getTasks().wait(20, () -> matchData.removeInactiveRanges(match));
		startTargetTask(match);
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);
		clearRanges(event.getMatch());
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		Match match = minigamer.getMatch();
		ArcheryMatchData matchData = match.getMatchData();
		String[] order = {"a", "b", "c", "d", "e", "f", "k", "l", "m", "n", "o", "r"};
		int ndx = 0;

		if (match.isStarted()) {
			int timeLeft = match.getTimer().getTime();

			lines.put("&1&fScore: &e" + minigamer.getScore(), 0);
			lines.put("&2&fTargets Hit: &e" + matchData.getTargetsHit(minigamer), 0);
			lines.put("&3", 0);
			lines.put("&4&fTime Left: &e&l" + timeLeft, 0);
			lines.put("&5", 0);

			Map<Minigamer, Integer> sortedScores = matchData.getSortedScores(match);
			for (Minigamer sbMinigamer : sortedScores.keySet()) {
				if (sbMinigamer.getScore() <= 0)
					continue;

				if (ndx >= order.length)
					break;

				String name = sbMinigamer.getNickname();
				if (minigamer.equals(sbMinigamer))
					name = "&f&lYOU";

				int score = sbMinigamer.getScore();
				lines.put("&" + order[ndx++] + "&f" + name + "&f: &e" + score, 0);
			}
		} else {
			for (Minigamer sbMinigamer : match.getMinigamers())
				lines.put(sbMinigamer.getNickname(), 0);
		}
		return lines;
	}

	public void startTargetTask(Match match) {
		ArcheryMatchData matchData = match.getMatchData();
		Map<ProtectedRegion, ArrayList<Location>> powderLocations = matchData.getPowderLocations();
		saveTargetSchems(match);
		Map<String, Clipboard> targetSchems = matchData.getTargetSchematics();

		match.getTasks().repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(7), () -> {
			AtomicInteger wait = new AtomicInteger(0);
			AtomicInteger count = new AtomicInteger(0);

			for (ProtectedRegion colorRegion : powderLocations.keySet()) {
				if (count.get() > 0 && count.get() % 3 == 0)
					wait.getAndIncrement();

				Tasks.wait(wait.get(), () -> {
					ArrayList<Location> locations = powderLocations.get(colorRegion);

					for (int i = 0; i < 10; i++) {
						Location randomXZ = RandomUtils.randomElement(locations);
						int min = colorRegion.getMinimumPoint().getBlockY() + 2;
						int max = colorRegion.getMaximumPoint().getBlockY() - 2;
						int y = RandomUtils.randomInt(min, max);

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
							Clipboard schem = targetSchems.get(key);
							match.worldedit().paster().clipboard(schem).at(targetLoc).pasteAsync();
							break;
						}
					}
				});

				count.getAndIncrement();
			}
		});
	}

	public void saveTargetSchems(Match match) {
		ArcheryMatchData matchData = match.getMatchData();
		ArcheryArena arena = match.getArena();
		Set<ProtectedRegion> targetRegions = arena.getTargetRegions();

		targetRegions.forEach(targetRegion ->
			match.worldedit().copy(match.worldguard().convert(targetRegion)).thenAccept(clipboard -> {
				Direction direction = matchData.getTargetDirection(targetRegion);
				String color = matchData.getTargetColor(targetRegion);
				String key = (direction.name() + "_" + color).toLowerCase();
				matchData.getTargetSchematics().put(key, clipboard);
			}));
	}

	public boolean canPlaceTarget(Location location) {
		List<Block> nearbyBlocks = BlockUtils.getBlocksInRadius(location, 3);
		for (Block block : nearbyBlocks) {
			if (MaterialTag.CONCRETES.isTagged(block.getType()) || block.getType().equals(Material.STONE_BUTTON))
				return false;
		}
		return true;
	}

	public void clearRanges(Match match) {
		ArcheryArena arena = match.getArena();
		for (int i = 1; i <= 10; i++) {
			Set<ProtectedRegion> rangeRegions = arena.getRegionsLike("range_[\\d]+_.*");
			rangeRegions.forEach(region -> {
				CuboidRegion expandedRegion = (CuboidRegion) match.worldedit().expandAll(match.worldguard().convert(region), 2);
				List<Block> blocks = match.worldedit().getBlocks(expandedRegion);
				blocks.forEach(block -> {
					if (block.getType().equals(Material.WHITE_CONCRETE))
						removeTarget(block);
				});
			});
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this)) return;

		Match match = minigamer.getMatch();
		if (!match.isStarted()) return;

		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) ||
				event.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||
				event.getAction().equals(Action.PHYSICAL))
			event.setCancelled(true);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Arrow))
			return;

		if (!new WorldGuardUtils(projectile).getRegionNamesAt(projectile.getLocation()).contains("archery"))
			return;

		Block hitBlock = event.getHitBlock();
		if (hitBlock == null)
			return;

		if (!hitBlock.getType().equals(Material.WHITE_CONCRETE))
			return;

		if (!(projectile.getShooter() instanceof Player player))
			return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		String color = ColorType.of(hitBlock.getRelative(0, 1, 0).getType()).getName();
		minigamer.scored(getPoints(color));

		ArcheryMatchData matchData = minigamer.getMatch().getMatchData();
		projectile.remove();
		removeTarget(hitBlock);
		matchData.addTargets(minigamer);
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);
		minigamer.getMatch().getScoreboard().update();
	}

	public int getPoints(String color) {
		return switch (color) {
			case "yellow" -> 1;
			case "orange" -> 3;
			case "red" -> 5;
			default -> 0;
		};
	}

	public void removeTarget(Block target) {
		Collection<Entity> entities = target.getWorld().getNearbyEntities(target.getLocation(), 2, 2, 2);
		for (Entity entity : entities)
			if (entity.getType().equals(EntityType.ARROW))
				entity.remove();

		List<Block> blocks = BlockUtils.getBlocksInRadius(target, 2);
		for (Block block : blocks)
			if (block.getType().equals(Material.STONE_BUTTON))
				block.setType(Material.AIR);

		for (Block block : blocks)
			if (MaterialTag.CONCRETES.isTagged(block.getType()))
				block.setType(Material.AIR);
	}
}
