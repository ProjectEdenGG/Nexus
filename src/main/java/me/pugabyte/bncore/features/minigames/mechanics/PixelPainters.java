package me.pugabyte.bncore.features.minigames.mechanics;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.PixelPaintersMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


// TODO:
//  - Scoreboards
//  - convert worldguard utils region stuff to arena region stuff
//  - lobby animation

public class PixelPainters extends TeamlessMechanic {
	WorldGuardUtils WGUtils = Minigames.getWorldGuardUtils();
	WorldEditUtils WEUtils = Minigames.getWorldEditUtils();
	@Getter
	private final int MAX_ROUNDS = 5;
	@Getter
	private final int TIME_OUT = 8 * 20;
	@Getter
	private final int ROUND_COUNTDOWN = 30 * 20;
	@Getter
	private Region NEXT_DESIGN_REGION = WGUtils.getRegion("pixelpainters_nextdesigntest");
	@Getter
	private Region LOGO_REGION = WGUtils.getRegion("pixelpainters_logo");

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
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();
		matchData.setCurrentRound(0);
		countDesigns(match);
		endOfRound(match);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		pasteLogo();
		clearFloors();
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
				lines.put("&2&fRound: &c" + matchData.getCurrentRound() + "&f/&c" + getMAX_ROUNDS(), 0);
				lines.put("&3&fNext Round In: &c" + matchData.getTimeLeft(), 0);


				// During Round
			} else {
				for (Minigamer minigamer : match.getMinigamers()) {
					if (matchData.getChecked().contains(minigamer))
						lines.put("&a" + minigamer.getName(), 1);
					else
						lines.put(minigamer.getName(), 1);

					lines.put("&a", 0);
					lines.put("&fTime Left: " + matchData.getTimeLeft(), 0);
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

		if (matchData.getCurrentRound() != 0) {
			minigamers.forEach(minigamer -> Utils.sendActionBar(minigamer.getPlayer(), "&cRound Over!"));
		}

		matchData.canCheck(false);
		matchData.getChecked().clear();

		minigamers.forEach(minigamer -> minigamer.getPlayer().getInventory().clear());
		setupNextDesign(match);

		// Start countdown to new round
		match.getTasks().wait(TIME_OUT / 2, () -> {
					pasteLogo();
					clearFloors();
					Tasks.Countdown countdown = Tasks.Countdown.builder()
							.duration(TIME_OUT)
							.onSecond(i -> minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
								if (match.isEnded()) {
									return;
								}
								matchData.setTimeLeft(i);
								match.getScoreboard().update();

								Utils.sendActionBar(player, "Next round starts in... " + i + " second" + (i != 1 ? "s" : ""));
								if (i <= 3)
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 10F, 0.5F);
							}))
							.onComplete(() -> newRound(match))
							.start();

					match.getTasks().register(countdown.getTaskId());
				}
		);

	}

	public void countDesigns(Match match) {
		Region designsRegion = WGUtils.getRegion("pixelpainters_designstest");
		PixelPaintersMatchData matchData = match.getMatchData();

		int area = designsRegion.getArea();
		EditSession editSession = WEUtils.getEditSession();
		int airCount = editSession.countBlocks(designsRegion, Collections.singleton(new BaseBlock(Material.AIR.getId())));
		int blocksCount = area - airCount;
		matchData.setDesignCount(blocksCount / 81);
	}

	public void newRound(Match match) {
		if (match.isEnded()) return; // just in case

		PixelPaintersMatchData matchData = match.getMatchData();
		matchData.setRoundOver(false);
		// Increase round counter
		matchData.setCurrentRound(matchData.getCurrentRound() + 1);

		matchData.setRoundStart(System.currentTimeMillis());
		pasteNewDesign();
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
			if (!canPlaceBlock(event)) {
				event.setCancelled(true);
			}
			return;
		}

		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		// TODO: 1.13+ Convert to material tags
		if (event.getClickedBlock() == null || !event.getClickedBlock().getType().toString().toLowerCase().contains("button"))
			return;

		if (matchData.getChecked().contains(minigamer)) return;

		pressButton(minigamer, event);
	}

	public void removeBlock(Minigamer minigamer, PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		Set<ProtectedRegion> regionsAt = WGUtils.getRegionsAt(block.getLocation());
		regionsAt.forEach(region -> {
			if (region.getId().matches("pixelpainters_floortest_[0-9]+")) {
				ItemStack item = new ItemStack(block.getType(), 1, block.getData());
				event.getClickedBlock().setType(Material.AIR);
				minigamer.getPlayer().getInventory().addItem(item);
			}
		});
	}

	public boolean canPlaceBlock(PlayerInteractEvent event) {
		Block block = event.getClickedBlock().getRelative(event.getBlockFace());
		Set<ProtectedRegion> regionsAt = WGUtils.getRegionsAt(block.getLocation());
		for (ProtectedRegion region : regionsAt) {
			if (region.getId().matches("pixelpainters_floortest_[0-9]+"))
				return true;
		}
		return false;
	}

	public void pressButton(Minigamer minigamer, PlayerInteractEvent event) {
		Match match = minigamer.getMatch();
		PixelPaintersMatchData matchData = match.getMatchData();

		// For Testing
		Location floorLoc = (event.getClickedBlock()).getRelative(0, -1, 0).getLocation();

//		Location floorLoc = (event.getClickedBlock()).getRelative(0, -1 ,3).getLocation();
		ProtectedRegion floorRg = null;
		Set<ProtectedRegion> regions = WGUtils.getRegionsAt(floorLoc);

		for (ProtectedRegion region : regions) {
			if (region.getId().matches("pixelpainters_floortest_[0-9]+")) {
				floorRg = region;
				break;
			}
		}

		if (floorRg == null)
			return;

		int incorrect = checkDesign((CuboidRegion) WGUtils.convert(floorRg), match);
		if (incorrect == 0) {
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
				endOfRound(match);
			}

		} else
			minigamer.tell("&e" + incorrect + " &3blocks incorrect!");

	}

	public void startRoundCountdown(Match match) {
		List<Minigamer> minigamers = match.getMinigamers();
		PixelPaintersMatchData matchData = match.getMatchData();
		Tasks.Countdown countdown = Tasks.Countdown.builder()
				.duration(ROUND_COUNTDOWN)
				.onSecond(i -> minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
					if (match.isEnded()) return;
					matchData.setTimeLeft(i);
					match.getScoreboard().update();

					Utils.sendActionBar(player, "Round ends in... " + i + " second" + (i != 1 ? "s" : ""));
					if (i <= 3)
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 10F, 0.5F);
				}))
				.onComplete(() -> endOfRound(match))
				.start();

		matchData.setRoundCountdownID(countdown.getTaskId());
		match.getTasks().register(countdown.getTaskId());
	}

	public void cancelCountdown(Match match) {
		PixelPaintersMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getRoundCountdownID());
	}

	// check the player's floor against the current design
	public int checkDesign(CuboidRegion floorRegion, Match match) {
		int incorrect = 0;
		PixelPaintersMatchData matchData = match.getMatchData();
		CuboidRegion designRegion = matchData.getDesignRegion();

		Vector floorMax = floorRegion.getMaximumPoint();
		Vector designMax = designRegion.getMaximumPoint();
		for (int z = 0; z < 9; z++) {
			for (int x = 0; x < 9; x++) {
				Vector floorV = floorMax.subtract(x, 0, z);
				Vector designV = designMax.subtract(x, 0, z);

				Block floorBlock = WEUtils.toLocation(floorV).getBlock();
				Block designBlock = WEUtils.toLocation(designV).getBlock();
				if (!(floorBlock.getType().equals(designBlock.getType()) && floorBlock.getData() == floorBlock.getData()))
					++incorrect;
			}
		}
		return incorrect;
	}

	// designsRegion Max is south east corner, designsRegion Min is north west corner
	public void setupNextDesign(Match match) {
		Region designsRegion = WGUtils.getRegion("pixelpainters_designstest");
		PixelPaintersMatchData matchData = match.getMatchData();

		int designCount = matchData.getDesignCount();
		int design = Utils.randomInt(1, designCount);
		for (int i = 0; i < matchData.getDesignCount(); i++) {
			design = Utils.randomInt(1, designCount);
			if (!matchData.getDesignsPlayed().contains(design))
				break;
		}
		matchData.getDesignsPlayed().add(design);

		// Get minimum point from current chosen design
		Vector designMin = designsRegion.getMinimumPoint().subtract(0, 1, 0).add(0, design, 0);

		// Get maximum point from total designs - difference from chosen design
		int diff = designCount - designMin.getBlockY();

		int yValue = designsRegion.getMinimumPoint().getBlockY() - 1;
		Vector designMax = designsRegion.getMaximumPoint().subtract(0, diff, 0).subtract(0, yValue, 0);
		CuboidRegion designRegion = new CuboidRegion(designMin.subtract(0, 1, 0), designMax.subtract(0, 1, 0));
		matchData.setDesignRegion(designRegion);

		Vector copySliceMin = designMin.add(0, 0, 8);
		Vector pasteMin = NEXT_DESIGN_REGION.getMinimumPoint();

		Vector copyMinV;
		Vector copyMaxV;
		Vector pasteMinV;

		for (int i = 0; i <= 9; i++) {
			copyMinV = copySliceMin.subtract(0, 0, i);
			copyMaxV = designMax.subtract(0, 0, i);
			pasteMinV = pasteMin.add(0, i, 0);
			Region copyRg = new CuboidRegion(WGUtils.getWorldEditWorld(), copyMinV, copyMaxV);
			Schematic schem = WEUtils.copy(copyRg);
			WEUtils.paste(schem, pasteMinV);
		}
	}

	public void pasteNewDesign() {
		Set<ProtectedRegion> wallRegions = WGUtils.getRegionsLike("pixelpainters_walltest_[0-9]+");
		Schematic schem = WEUtils.copy(NEXT_DESIGN_REGION);
		wallRegions.forEach(wallRegion -> {
			Region region = WGUtils.convert(wallRegion);
			WEUtils.paste(schem, region.getMinimumPoint());
		});
	}

	public void giveBlocks(Match match) {
		List<Block> blocks = WEUtils.getBlocks((CuboidRegion) NEXT_DESIGN_REGION);

		List<ItemStack> items = new ArrayList<>();
		for (Block block : blocks) {
			items.add(new ItemStack(block.getType(), 1, block.getData()));
		}

		List<Minigamer> minigamers = match.getMinigamers();
		minigamers.forEach(minigamer -> Utils.giveItems(minigamer.getPlayer(), items));

	}

	public void pasteLogo() {
		// Paste Logo on all island walls
		Set<ProtectedRegion> wallRegions = WGUtils.getRegionsLike("pixelpainters_walltest_[0-9]+");
		Schematic schem = WEUtils.copy(LOGO_REGION);
		wallRegions.forEach(wallRegion -> {
			Region region = WGUtils.convert(wallRegion);
			WEUtils.paste(schem, region.getMinimumPoint());
		});
	}

	public void clearFloors() {
		Set<ProtectedRegion> floorRegions = WGUtils.getRegionsLike("pixelpainters_floortest_[0-9]+");
		floorRegions.forEach(floorRegion -> {
			Region region = WGUtils.convert(floorRegion);
			WEUtils.fill(region, Material.AIR);
		});
	}
}
