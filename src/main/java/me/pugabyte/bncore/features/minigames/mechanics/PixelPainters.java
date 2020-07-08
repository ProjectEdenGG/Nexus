package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.PixelPaintersArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.PixelPaintersMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ActionBarUtils;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks.Countdown;
import me.pugabyte.bncore.utils.Tasks.Countdown.CountdownBuilder;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.RandomUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.bncore.utils.StringUtils.plural;

// TODO:
//  - Only paste the designs on active islands

public class PixelPainters extends TeamlessMechanic {
	private final int MAX_ROUNDS = 5;
	private final int TIME_BETWEEN_ROUNDS = 8 * 20;
	private final int ROUND_COUNTDOWN = 45 * 20;

	@Override
	public String getName() {
		return "Pixel Painters";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.PAINTING);
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);
		Match match = event.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();
		if (!matchData.isAnimateLobby()) {
			matchData.setAnimateLobby(true);
			startLobbyAnimation(match);
		}
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		super.onQuit(event);
		Match match = event.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();
		if (matchData.isAnimateLobby() && match.getMinigamers().size() == 0)
			matchData.setAnimateLobby(false);
	}

	// designsRegion Max is south east corner, designsRegion Min is north west corner
	public void startLobbyAnimation(Match match) {
		PixelPaintersMatchData matchData = match.getMatchData();
		PixelPaintersArena arena = match.getArena();
		matchData.setLobbyDesign(0);
		countDesigns(match);
		int taskId = match.getTasks().repeat(0, Time.SECOND.x(2), () -> {
			if (match.isEnded() || match.isStarted())
				return;

			// Build Next Design
			Region designsRegion = arena.getDesignRegion();

			int designCount = matchData.getDesignCount();
			int design = RandomUtils.randomInt(1, designCount);
			for (int i = 0; i < designCount; i++) {
				design = RandomUtils.randomInt(1, designCount);
				if (matchData.getLobbyDesign() != design)
					break;
			}
			matchData.setLobbyDesign(design);

			// Get minimum point from current chosen design
			BlockVector3 designMin = designsRegion.getMinimumPoint().subtract(0, 1, 0).add(0, design, 0);

			// Get maximum point from: 255 - MinPoint
			int diff = designsRegion.getMaximumPoint().getBlockY() - designMin.getBlockY();

			BlockVector3 designMax = designsRegion.getMaximumPoint().subtract(0, diff, 0);

			BlockVector3 copySliceMin = designMin.add(0, 0, 8);
			BlockVector3 pasteMin = arena.getLobbyDesignRegion().getMinimumPoint();

			BlockVector3 copyMinV;
			BlockVector3 copyMaxV;
			BlockVector3 pasteMinV;

			for (int i = 0; i < 9; i++) {
				copyMinV = copySliceMin.subtract(0, 0, i);
				copyMaxV = designMax.subtract(0, 0, i);
				pasteMinV = pasteMin.add(0, i, 0);
				Region copyRg = new CuboidRegion(match.getWGUtils().getWorldEditWorld(), copyMinV, copyMaxV);
				Clipboard schem = match.getWEUtils().copy(copyRg);
				match.getWEUtils().paster().clipboard(schem).at(pasteMinV).paste();
			}

			// Paste Design
			Region pasteRegion = arena.getLobbyAnimationRegion();
			Clipboard schem = match.getWEUtils().copy(arena.getLobbyDesignRegion());
			match.getWEUtils().paster().clipboard(schem).at(pasteRegion.getMinimumPoint()).paste();
		});
		matchData.setAnimateLobbyId(taskId);
	}

	@Override
	public void onStart(MatchStartEvent event) {
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
	public void onEnd(MatchEndEvent event) {
		pasteLogo(event.getMatch());
		clearFloors(event.getMatch());
		super.onEnd(event);
	}

	@Override
	public Map<String, Integer> getScoreboardLines(Match match) {
		Map<String, Integer> lines = new HashMap<>();
		PixelPaintersMatchData matchData = match.getMatchData();
		// During Game
		if (match.isStarted()) {

			// Inbetween Rounds
			if (matchData.isRoundOver()) {
				for (Minigamer minigamer : match.getMinigamers()) {
					lines.put(minigamer.getName(), minigamer.getScore());
				}
				lines.put("&1", 0);
				lines.put("&2&fRound: &c" + matchData.getCurrentRound() + "&f/&c" + MAX_ROUNDS, 0);

				int timeLeft = matchData.getTimeLeft();
				if (timeLeft <= 0)
					lines.put("&3&fNext Round In: &c", 0);
				else
					lines.put("&3&fNext Round In: &c" + timeLeft, 0);

				// During Round
			} else {
				for (Minigamer minigamer : match.getMinigamers()) {
					if (matchData.getChecked().contains(minigamer))
						lines.put("&1&a" + minigamer.getName(), 0);
					else
						lines.put("&1&f" + minigamer.getName(), 0);

					lines.put("&2", 0);

					int timeLeft = matchData.getTimeLeft();
					if (timeLeft <= 0)
						lines.put("&3&fTime Left: ", 0);
					else
						lines.put("&4&fTime Left: &c" + timeLeft, 0);
				}
			}

			// In Lobby
		} else {
			for (Minigamer minigamer : match.getMinigamers())
				lines.put(minigamer.getColoredName(), 0);
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
			minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
				ActionBarUtils.sendActionBar(player, "&c&lRound Over!");
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.7F);
			});
			match.broadcast("&c&lRound Over!");
		}

		matchData.canCheck(false);
		matchData.getChecked().clear();

		minigamers.forEach(minigamer -> minigamer.getPlayer().getInventory().clear());
		setupNextDesign(match);

		if (matchData.getCurrentRound() == MAX_ROUNDS) {
			match.getTasks().wait(3 * 20, () -> {
				minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
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
						.onSecond(i -> minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
							if (match.isEnded())
								return;

							matchData.setTimeLeft(i);
							match.getScoreboard().update();

							ActionBarUtils.sendActionBar(player, "&cNext round starts in...&c&l " + plural(i + " second", i));
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
		Location min = arena.getWEUtils().toLocation(arena.getDesignRegion().getMinimumPoint());
		int highest = min.getWorld().getHighestBlockYAt(min);
		matchData.setDesignCount(highest - 4);
	}

	public void newRound(Match match) {
		if (match.isEnded()) return; // just in case

		PixelPaintersMatchData matchData = match.getMatchData();
		matchData.setRoundOver(false);
		matchData.setTimeLeft(0);
		match.getScoreboard().update();

		// Increase round counter
		matchData.setCurrentRound(matchData.getCurrentRound() + 1);

		matchData.setRoundStart(System.currentTimeMillis());
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

		if (!event.getHand().equals(EquipmentSlot.HAND))
			return;

		Match match = minigamer.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();

		if (!matchData.canCheck()) return;

		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			event.setCancelled(true);
			removeBlock(minigamer, event);
			return;
		}

		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !Utils.isNullOrAir(event.getItem())) {
			if (!canPlaceBlock(minigamer, event)) {
				event.setCancelled(true);
			}
			return;
		}

		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		if (event.getClickedBlock() == null || !MaterialTag.BUTTONS.isTagged(event.getClickedBlock().getType()))
			return;

		if (matchData.getChecked().contains(minigamer)) return;

		pressButton(minigamer, event);
	}

	public void removeBlock(Minigamer minigamer, PlayerInteractEvent event) {
		PixelPaintersArena arena = minigamer.getMatch().getArena();
		Block block = event.getClickedBlock();
		Set<ProtectedRegion> regionsAt = minigamer.getMatch().getWGUtils().getRegionsAt(block.getLocation());
		regionsAt.forEach(region -> {
			if (region.getId().matches(arena.getRegionTypeRegex("floor"))) {
				ItemStack item = new ItemStack(block.getType());
				event.getClickedBlock().setType(Material.AIR);
				Player player = minigamer.getPlayer();
				player.getInventory().addItem(item);
				player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 10F, 1F);
			}
		});
	}

	public boolean canPlaceBlock(Minigamer minigamer, PlayerInteractEvent event) {
		PixelPaintersArena arena = minigamer.getMatch().getArena();
		Block block = event.getClickedBlock().getRelative(event.getBlockFace());
		Set<ProtectedRegion> regionsAt = minigamer.getMatch().getWGUtils().getRegionsAt(block.getLocation());
		for (ProtectedRegion region : regionsAt) {
			if (region.getId().matches(arena.getRegionTypeRegex("floor")))
				return true;
		}
		return false;
	}

	public void pressButton(Minigamer minigamer, PlayerInteractEvent event) {
		Match match = minigamer.getMatch();
		PixelPaintersArena arena = match.getArena();
		PixelPaintersMatchData matchData = match.getMatchData();

		Location floorLoc = (event.getClickedBlock()).getRelative(0, -1, 3).getLocation();
		ProtectedRegion floorRg = null;
		Set<ProtectedRegion> regions = match.getWGUtils().getRegionsAt(floorLoc);

		for (ProtectedRegion region : regions) {
			if (region.getId().matches(arena.getRegionTypeRegex("floor"))) {
				floorRg = region;
				break;
			}
		}

		if (floorRg == null)
			return;

		int incorrect = checkDesign((CuboidRegion) match.getWGUtils().convert(floorRg), match);
		Player player = minigamer.getPlayer();
		if (incorrect == 0) {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10F, 0.5F);
			long miliseconds = System.currentTimeMillis() - matchData.getRoundStart();
			long seconds = miliseconds / 1000;
			match.broadcast("&a" + minigamer.getName() + " &2finished in &a" + seconds + " &2seconds!");
			matchData.getChecked().add(minigamer);

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
			minigamer.tell("&e" + incorrect + " &3blocks incorrect!");
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
		}

	}

	public void startRoundCountdown(Match match) {
		PixelPaintersMatchData matchData = match.getMatchData();

		List<Minigamer> minigamers = match.getMinigamers();
		minigamers.stream().map(Minigamer::getPlayer).forEach(player ->
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F));

		CountdownBuilder countdown = Countdown.builder()
				.duration(ROUND_COUNTDOWN)
				.onSecond(i -> minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
					if (match.isEnded()) return;
					matchData.setTimeLeft(i);
					match.getScoreboard().update();

					ActionBarUtils.sendActionBar(player, "&cRound ends in...&c&l " + plural(i + " second", i));
					if (i <= 3)
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F);
				}))
				.onComplete(() -> endOfRound(match));

		matchData.setRoundCountdownId(match.getTasks().countdown(countdown));
	}

	public void cancelCountdown(Match match) {
		PixelPaintersMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getRoundCountdownId());
		matchData.setTimeLeft(0);
		match.getScoreboard().update();
	}

	// check the player's floor against the current design
	public int checkDesign(CuboidRegion floorRegion, Match match) {
		int incorrect = 0;
		PixelPaintersMatchData matchData = match.getMatchData();
		CuboidRegion designRegion = matchData.getDesignRegion();

		BlockVector3 floorMax = floorRegion.getMaximumPoint();
		BlockVector3 designMax = designRegion.getMaximumPoint();
		for (int z = 0; z < 9; z++) {
			for (int x = 0; x < 9; x++) {
				BlockVector3 floorV = floorMax.subtract(x, 0, z);
				BlockVector3 designV = designMax.subtract(x, 0, z);

				Block floorBlock = match.getWEUtils().toLocation(floorV).getBlock();
				Block designBlock = match.getWEUtils().toLocation(designV).getBlock();
				if (!floorBlock.getType().equals(designBlock.getType()))
					++incorrect;
			}
		}
		return incorrect;
	}

	// designsRegion Max is south east corner, designsRegion Min is north west corner
	public void setupNextDesign(Match match) {
		PixelPaintersArena arena = match.getArena();
		Region designsRegion = arena.getDesignRegion();
		PixelPaintersMatchData matchData = match.getMatchData();

		int designCount = matchData.getDesignCount();
		int design = RandomUtils.randomInt(1, designCount);
		for (int i = 0; i < designCount; i++) {
			design = RandomUtils.randomInt(1, designCount);
			if (!matchData.getDesignsPlayed().contains(design))
				break;
		}
		matchData.getDesignsPlayed().add(design);

		// Get minimum point from current chosen design
		BlockVector3 designMin = designsRegion.getMinimumPoint().subtract(0, 1, 0).add(0, design, 0);

		// Get maximum point from: 255 - MinPoint
		int diff = designsRegion.getMaximumPoint().getBlockY() - designMin.getBlockY();

		BlockVector3 designMax = designsRegion.getMaximumPoint().subtract(0, diff, 0);
		CuboidRegion designRegion = new CuboidRegion(designMin, designMax);
		matchData.setDesignRegion(designRegion);

		BlockVector3 copySliceMin = designMin.add(0, 0, 8);
		BlockVector3 pasteMin = arena.getNextDesignRegion().getMinimumPoint();

		BlockVector3 copyMinV;
		BlockVector3 copyMaxV;
		BlockVector3 pasteMinV;

		for (int i = 0; i < 9; i++) {
			copyMinV = copySliceMin.subtract(0, 0, i);
			copyMaxV = designMax.subtract(0, 0, i);
			pasteMinV = pasteMin.add(0, i, 0);
			Region copyRg = new CuboidRegion(match.getWGUtils().getWorldEditWorld(), copyMinV, copyMaxV);
			Clipboard schem = match.getWEUtils().copy(copyRg);
			match.getWEUtils().paster().clipboard(schem).at(pasteMinV).paste();
		}
	}

	public void pasteNewDesign(Match match) {
		PixelPaintersArena arena = match.getArena();
		Set<ProtectedRegion> wallRegions = arena.getRegionsLike("wall_[0-9]+");
		Clipboard schem = match.getWEUtils().copy(arena.getNextDesignRegion());
		wallRegions.forEach(wallRegion -> {
			Region region = match.getWGUtils().convert(wallRegion);
			match.getWEUtils().paster().clipboard(schem).at(region.getMinimumPoint()).paste();
		});
	}

	public void giveBlocks(Match match) {
		PixelPaintersArena arena = match.getArena();
		List<Block> blocks = match.getWEUtils().getBlocks((CuboidRegion) arena.getNextDesignRegion());

		List<ItemStack> items = new ArrayList<>();
		for (Block block : blocks)
			items.add(new ItemStack(block.getType(), 1));

		List<Minigamer> minigamers = match.getMinigamers();
		minigamers.forEach(minigamer -> Utils.giveItems(minigamer.getPlayer(), items));
	}

	public void pasteLogo(Match match) {
		PixelPaintersArena arena = match.getArena();
		// Paste Logo on all island walls
		Set<ProtectedRegion> wallRegions = arena.getRegionsLike("wall_[0-9]+");
		Clipboard schem = match.getWEUtils().copy(arena.getLogoRegion());
		wallRegions.forEach(wallRegion -> {
			Region region = match.getWGUtils().convert(wallRegion);
			match.getWEUtils().paster().clipboard(schem).at(region.getMinimumPoint()).paste();
		});
	}

	public void clearFloors(Match match) {
		PixelPaintersArena arena = match.getArena();
		Set<ProtectedRegion> floorRegions = arena.getRegionsLike("floor_[0-9]+");
		floorRegions.forEach(floorRegion -> {
			Region region = match.getWGUtils().convert(floorRegion);
			match.getWEUtils().set(region, BlockTypes.AIR);
		});
	}
}
