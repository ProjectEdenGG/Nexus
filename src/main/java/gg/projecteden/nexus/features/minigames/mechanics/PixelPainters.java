package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.arenas.PixelPaintersArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.PixelPaintersMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.models.statistics.PixelPaintersStatistics;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.Countdown;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@MatchStatisticsClass(PixelPaintersStatistics.class)
public class PixelPainters extends TeamlessMechanic {
	private static final int MAX_ROUNDS = 5;
	private static final int TIME_BETWEEN_ROUNDS = 8 * 20;
	private static final int ROUND_COUNTDOWN = 45 * 20;
	private static final AffineTransform ROTATION = new AffineTransform().rotateY(270).rotateX(-90).rotateZ(90);
	private static final AffineTransform FLIP = new AffineTransform().rotateY(180);

	@Override
	public @NotNull String getName() {
		return "Pixel Painters";
	}

	@Override
	public @NotNull String getDescription() {
		return "Re-create the designs in front of you as fast as you can";
	}

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.PAINTING);
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Match match = event.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();
		if (!matchData.isAnimateLobby()) {
			matchData.setAnimateLobby(true);
			startLobbyAnimation(match);
		}
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);
		Match match = event.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();
		if (matchData.isAnimateLobby() && match.getMinigamers().isEmpty())
			matchData.setAnimateLobby(false);
	}

	public void startLobbyAnimation(Match match) {
		PixelPaintersMatchData matchData = match.getMatchData();
		PixelPaintersArena arena = match.getArena();
		matchData.setLobbyDesign(0);
		countDesigns(match);
		int taskId = match.getTasks().repeatAsync(0, TickTime.SECOND.x(2), () -> {
			if (match.isEnded() || match.isStarted())
				return;

			match.worldedit().paster("Pasting lobby design")
				.clipboard(matchData.getRandomLobbyDesign())
				.at(arena.getLobbyAnimationRegion().getMinimumPoint())
				.transform(ROTATION)
				.pasteAsync();
		});
		matchData.setAnimateLobbyId(taskId);
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		pasteLogo(event.getMatch());
		super.onInitialize(event);
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getAnimateLobbyId());
		matchData.setCurrentRound(0);
		matchData.setTimeLeft(0);
		countDesigns(match);
		endOfRound(match);
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		pasteLogo(event.getMatch());
		clearFloors(event.getMatch());
		super.onEnd(event);
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		PixelPaintersMatchData matchData = match.getMatchData();
		// During Game
		if (match.isStarted()) {

			// Inbetween Rounds
			if (matchData.isRoundOver()) {
				for (Minigamer minigamer : match.getMinigamers().stream().sorted((minigamer, t1) -> t1.getScore() - minigamer.getScore()).toList()) {
					lines.put(minigamer.getNickname(), minigamer.getScore());
				}
				lines.put("&1", Integer.MIN_VALUE);
				lines.put("&2&fRound: &c" + matchData.getCurrentRound() + "&f/&c" + MAX_ROUNDS, Integer.MIN_VALUE);

				long timeLeft = matchData.getTimeLeft();
				if (timeLeft <= 0)
					lines.put("&3&fNext Round In: &c", Integer.MIN_VALUE);
				else
					lines.put("&3&fNext Round In: &c" + timeLeft, Integer.MIN_VALUE);

				// During Round
			} else {
				match.getMinigamers().stream().filter(minigamer -> matchData.getChecked().contains(minigamer))
					.forEach(minigamer -> lines.put("&1&a" + minigamer.getNickname(), Integer.MIN_VALUE));

				match.getMinigamers().stream().filter(minigamer -> !matchData.getChecked().contains(minigamer))
					.forEach(minigamer -> lines.put("&1&f" + minigamer.getNickname(), Integer.MIN_VALUE));

				lines.put("&2", Integer.MIN_VALUE);

				long timeLeft = matchData.getTimeLeft();
				if (timeLeft <= 0)
					lines.put("&3&fTime Left: ", Integer.MIN_VALUE);
				else
					lines.put("&4&fTime Left: &c" + timeLeft, Integer.MIN_VALUE);
			}

			// In Lobby
		} else {
			for (Minigamer minigamer : match.getMinigamers())
				lines.put(minigamer.getVanillaColoredName(), Integer.MIN_VALUE);
		}

		return lines;
	}

	public void endOfRound(Match match) {
		// Disable checking & Clear checked
		PixelPaintersMatchData matchData = match.getMatchData();
		List<Minigamer> minigamers = match.getMinigamers();
		matchData.setRoundOver(true);
		matchData.setTimeLeft(0);

		if (matchData.getCurrentRound() != 0) {
			minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player -> {
				ActionBarUtils.sendActionBar(player, "&c&lRound Over!");
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.7F);
			});
			match.broadcast("&c&lRound Over!");
		}

		matchData.canCheck(false);
		matchData.getChecked().clear();

		minigamers.forEach(minigamer -> minigamer.getOnlinePlayer().getInventory().clear());
		setupNextDesign(match);

		if (matchData.getCurrentRound() == MAX_ROUNDS) {
			match.getTasks().wait(3 * 20, () -> {
				minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player -> {
					ActionBarUtils.sendActionBar(player, "&c&lGame Over!");
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 1F);
					match.getTasks().wait(20, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.85F));
					match.getTasks().wait(40, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.65F));
				});
				match.broadcast("&c&lGame Over!");
				match.getTasks().wait(3 * 20, match::end);
			});
		} else {
			// Start countdown to new round
			match.getTasks().wait(TIME_BETWEEN_ROUNDS / 2, () -> {
				pasteLogo(match);
				clearFloors(match);
				match.getTasks().countdown(Countdown.builder()
					.duration(TIME_BETWEEN_ROUNDS)
					.onSecond(i -> minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player -> {
						if (match.isEnded())
							return;

						matchData.setTimeLeft(i);
						match.getScoreboard().update();

						ActionBarUtils.sendActionBar(player, "&cNext round starts in...&c&l " + StringUtils.plural(i + " second", i));
						if (i <= 3)
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F);
					}))
					.onComplete(() -> newRound(match)));
			});
		}
	}

	public void countDesigns(Match match) {
		PixelPaintersArena arena = match.getArena();
		PixelPaintersMatchData matchData = match.getMatchData();
		Location max = arena.worldedit().toLocation(arena.getDesignsRegion().getMaximumPoint());

		int minHeight = match.getWorld().getMinHeight();
		int highest = minHeight;

		for (int y = max.getBlockY(); y > minHeight; y--) {
			Location clone = max.clone();
			clone.setY(y);
			if (clone.getBlock().getType() == Material.AIR)
				continue;

			highest = y;
			break;
		}

		if (highest == minHeight)
			throw new InvalidInputException("Could not find any designs");

		matchData.setDesignCount(highest);
	}

	public void newRound(Match match) {
		if (match.isEnded()) return; // just in case

		PixelPaintersMatchData matchData = match.getMatchData();
		matchData.setRoundOver(false);
		matchData.setTimeLeft(0);
		match.getScoreboard().update();

		// Increase round counter
		matchData.setCurrentRound(matchData.getCurrentRound() + 1);

		matchData.setRoundStart(LocalDateTime.now());
		pasteNewDesign(match);
		giveBlocks(match);

		// Enable checking
		matchData.canCheck(true);
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);

		if (!minigamer.isPlaying(this))
			return;

		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND))
			return;

		Match match = minigamer.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();

		if (!matchData.canCheck()) return;

		if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
			if (event.getClickedBlock() != null && MaterialTag.BUTTONS.isTagged(event.getClickedBlock().getType())) {
				if (matchData.getChecked().contains(minigamer))
					return;

				Directional directional = (Directional) event.getClickedBlock().getBlockData();
				pressButton(minigamer, event, directional.getFacing());
				return;
			}

			Block placed = event.getClickedBlock().getRelative(event.getBlockFace());
			if (!canBuild(minigamer, placed))
				event.setCancelled(true);
			return;
		}

		if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
			event.setCancelled(true);
			removeBlock(minigamer, event.getClickedBlock());
		}
	}

	public void removeBlock(Minigamer minigamer, Block block) {
		if (!canBuild(minigamer, block)) return;
		ItemStack item = new ItemStack(block.getType());
		block.setType(Material.AIR);
		Player player = minigamer.getOnlinePlayer();
		player.getInventory().addItem(item);
		player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 10F, 1F);
	}

	public boolean canBuild(Minigamer minigamer, Block block) {
		return !minigamer.getMatch().getArena().getRegionsLikeAt("floor", block.getLocation()).isEmpty();
	}

	public void pressButton(Minigamer minigamer, PlayerInteractEvent event, BlockFace direction) {
		Validate.notNull(event.getClickedBlock(), "Clicked block should be insured non-null by calling function");
		Match match = minigamer.getMatch();
		PixelPaintersArena arena = match.getArena();
		PixelPaintersMatchData matchData = match.getMatchData();

		Location floorLoc = event.getClickedBlock().getRelative(direction, 4).getRelative(BlockFace.DOWN).getLocation();
		ProtectedRegion floorRg = null;
		Set<ProtectedRegion> regions = match.worldguard().getRegionsAt(floorLoc);

		for (ProtectedRegion region : regions) {
			if (region.getId().matches(arena.getRegionTypeRegex("floor"))) {
				floorRg = region;
				break;
			}
		}

		if (floorRg == null)
			return;

		int incorrect = checkDesign((CuboidRegion) match.worldguard().convert(floorRg), match, getRegionNumber(floorRg));
		Player player = minigamer.getOnlinePlayer();
		if (incorrect == 0) {
			String guessTime = StringUtils.getTimeFormat(Duration.between(matchData.getRoundStart(), LocalDateTime.now()));

			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10F, 0.5F);
			match.broadcast("&e" + minigamer.getNickname() + " &3finished in &e" + guessTime + "&3!");

			matchData.getChecked().add(minigamer);
			match.getMatchStatistics().award(PixelPaintersStatistics.BUILDS_COMPLETED, minigamer);

			int size = matchData.getChecked().size();
			minigamer.scored(Math.max(1, 1 + (4 - size)));
			match.getScoreboard().update();
			if (size == 1)
				startRoundCountdown(match);

			if (match.getMinigamers().size() == matchData.getChecked().size()) {
				cancelCountdown(match);
				matchData.canCheck(false);
				match.getTasks().wait(2 * 20, () -> endOfRound(match));
			}

		} else {
			minigamer.tell("&c" + incorrect + " &3blocks incorrect!");
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
		}
	}

	private static int getRegionNumber(ProtectedRegion floorRg) {
		return Integer.parseInt(StringUtils.listLast(floorRg.getId(), "_"));
	}

	public void startRoundCountdown(Match match) {
		PixelPaintersMatchData matchData = match.getMatchData();

		List<Minigamer> minigamers = match.getMinigamers();
		minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player ->
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F));

		matchData.setRoundCountdownId(match.getTasks().countdown(Countdown.builder()
			.duration(ROUND_COUNTDOWN)
			.onSecond(i -> minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player -> {
				if (match.isEnded()) return;
				matchData.setTimeLeft(i);
				match.getScoreboard().update();

				ActionBarUtils.sendActionBar(player, "&cRound ends in...&c&l " + StringUtils.plural(i + " second", i));
				if (i <= 3)
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F);
			}))
			.onComplete(() -> endOfRound(match))));
	}

	public void cancelCountdown(Match match) {
		PixelPaintersMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getRoundCountdownId());
		matchData.setTimeLeft(0);
		match.getScoreboard().update();
	}

	// check the player's floor against the current design
	public int checkDesign(CuboidRegion floorRegion, Match match, int floorId) {
		int incorrect = 0;
		PixelPaintersMatchData matchData = match.getMatchData();
		Region designRegion = matchData.getDesignRegion();

		BlockVector3 floorMin = floorRegion.getMinimumPoint();
		BlockVector3 designMin = designRegion.getMinimumPoint();
		for (int z = 0; z < 9; z++) {
			for (int x = 0; x < 9; x++) {
				BlockVector3 floorV = floorMin.add(x, 0, z);
				BlockVector3 designV;

				if (floorId % 2 == 0) {
					designV = designMin.add(8 - x, 0, z);
				} else {
					designV = designMin.add(x, 0, 8 - z);
				}

				Material floor = match.worldedit().toLocation(floorV).getBlock().getType();
				Material design = match.worldedit().toLocation(designV).getBlock().getType();

				if (!floor.equals(design)) {
					++incorrect;
				}
			}
		}

		return incorrect;
	}

	public void setupNextDesign(Match match) {
		PixelPaintersArena arena = match.getArena();
		PixelPaintersMatchData matchData = match.getMatchData();

		var min = match.worldedit().toLocation(arena.getNextDesignRegion().getMinimumPoint());
		var max = match.worldedit().toLocation(arena.getNextDesignRegion().getMaximumPoint());

		Dev.GRIFFIN.debug(new JsonBuilder("Design Min: " + min).command("//pos1 " + min.getX() + "," + min.getY() + "," + min.getZ()));
		Dev.GRIFFIN.debug(new JsonBuilder("Design Max: " + max).command("//pos2 " + max.getX() + "," + max.getY() + "," + max.getZ()));

		match.worldedit().paster("Setting up next design")
			.clipboard(matchData.getRandomGameDesign())
			.at(arena.getNextDesignRegion().getMinimumPoint())
			.transform(ROTATION)
			.pasteAsync();
	}

	public void pasteNewDesign(Match match) {
		PixelPaintersArena arena = match.getArena();
		for (int i = 1; i <= match.getAllMinigamers().size(); i++)
			match.worldedit().paster("Pasting new design " + i)
				.clipboard(arena.getNextDesignRegion())
				.at(arena.getRegion("wall_" + i).getMinimumPoint())
				.transform(i % 2 == 0 ? FLIP : null)
				.pasteAsync();
	}

	public void giveBlocks(Match match) {
		PixelPaintersArena arena = match.getArena();
		List<Block> blocks = match.worldedit().getBlocks(arena.getNextDesignRegion());

		List<ItemStack> items = new ArrayList<>();
		for (Block block : blocks)
			items.add(new ItemStack(block.getType(), 1));

		List<Minigamer> minigamers = match.getMinigamers();
		minigamers.forEach(minigamer -> PlayerUtils.giveItems(minigamer.getOnlinePlayer(), items));
	}

	// Paste Logo on all island walls
	public void pasteLogo(Match match) {
		PixelPaintersArena arena = match.getArena();
		Set<ProtectedRegion> wallRegions = arena.getRegionsLike("wall_[\\d]+");
		AtomicInteger wait = new AtomicInteger();
		wallRegions.forEach(wallRegion ->
			Tasks.wait(wait.getAndAdd(2), () ->
				match.worldedit().paster("Pasting logo")
					.clipboard(arena.getLogoRegion())
					.at(match.worldguard().convert(wallRegion).getMinimumPoint())
					.transform(getRegionNumber(wallRegion) % 2 == 0 ? FLIP : null)
					.pasteAsync()));
	}

	public void clearFloors(Match match) {
		PixelPaintersArena arena = match.getArena();
		Set<ProtectedRegion> floorRegions = arena.getRegionsLike("floor_[\\d]+");
		floorRegions.forEach(floorRegion -> {
			Region region = match.worldguard().convert(floorRegion);
			match.worldedit().set(region, BlockTypes.AIR);
		});
	}
}
