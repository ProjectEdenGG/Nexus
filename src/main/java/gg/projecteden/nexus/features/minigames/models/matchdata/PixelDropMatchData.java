package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.mechanics.PixelDrop;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.arenas.PixelDropArena;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.utils.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Data
@MatchDataFor(PixelDrop.class)
public class PixelDropMatchData extends MatchData {
	private List<Minigamer> guessed = new ArrayList<>();

	private Map<String, Block> designMap = new HashMap<>();
	private List<String> designKeys = new ArrayList<>();
	private int designSize;
	private int designTaskId;

	private static int designRegionLowestY = 63;
	private List<Design> gameDesigns = new ArrayList<>();
	private Design roundDesign;
	private List<Design> designsPlayed = new ArrayList<>();

	private String roundWord;
	private BossBar bossBar = null;
	private int wordTaskId;
	private int currentRound;
	private LocalDateTime roundStart;
	private long timeLeft;
	private int roundCountdownId;
	private boolean roundOver;
	private boolean canGuess;

	private Map<String, Block> lobbyDesignMap = new HashMap<>();
	private List<String> lobbyKeys = new ArrayList<>();
	private int nextFrameTaskId;
	private boolean doNextFrame;
	private boolean animateLobby;
	private Design lobbyDesign;
	private int animateLobbyId;

	public PixelDropMatchData(Match match) {
		super(match);
	}

	public void setupGame(Match match) {
		setCurrentRound(0);
		setTimeLeft(0);
		clearFloor(match);
	}

	public void endRound() {
		setRoundOver(true);
		setCanGuess(false);
		setTimeLeft(0);
		if (bossBar != null)
			match.hideBossBar(bossBar);
	}

	public void resetRound() {
		getDesignMap().clear();
		getDesignKeys().clear();
		getGuessed().clear();
	}

	public void setupRound(Match match) {
		setRoundOver(false);
		setCanGuess(false);
		setTimeLeft(0);
		match.getScoreboard().update();
		setCurrentRound(getCurrentRound() + 1);
		setRoundStart(LocalDateTime.now());
	}

	public void setDesign(Design design) {
		roundDesign = design;
		roundWord = design.getWord();
	}

	public void startLobbyAnimation(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		PixelDropArena arena = match.getArena();

		loadDesigns(match);
		lobbyDesign = getGameDesigns().get(0);
		doNextFrame = true;

		int animateTaskId = match.getTasks().repeat(0, TickTime.SECOND.x(2), () -> {
			if (match.isEnded() || match.isStarted() || !doNextFrame)
				return;
			doNextFrame = false;

			// Get Random Design
			//Region designsRegion = arena.getDesignRegion();
			Region lobbyAnimationRegion = arena.getLobbyAnimationRegion();

			Design lobbyDesign = getRandomLobbyDesign();
			matchData.setLobbyDesign(lobbyDesign);

			// Get min point of paste region
			Location pasteMin = worldedit().toLocation(lobbyAnimationRegion.getMinimumPoint());

			// Builds the map
			for (int x = 0; x < 15; x++) {
				for (int z = 0; z < 15; z++) {
					Block block = lobbyDesign.getMin().clone().add(x, 0, z).getBlock();
					String key = x + "_" + z;
					lobbyDesignMap.put(key, block);
					lobbyKeys.add(key);
				}
			}

			// Random Paste
			int nextFrameTaskId = match.getTasks().repeat(0, 2, () -> {
				for (int i = 0; i < 3; i++) {
					if (lobbyKeys.isEmpty()) {
						stopFrameTask(match);
						return;
					}

					String key = RandomUtils.randomElement(lobbyKeys);
					lobbyKeys.remove(key);
					String[] xz = key.split("_");
					int x = Integer.parseInt(xz[0]);
					int z = Integer.parseInt(xz[1]);

					Block block = lobbyDesignMap.get(x + "_" + z);
					Location loc = pasteMin.clone().add(x, 0, z);

					loc.getBlock().setType(block.getType());
					loc.getBlock().setBlockData(block.getBlockData());
				}
			});
			matchData.setNextFrameTaskId(nextFrameTaskId);
		});
		matchData.setAnimateLobbyId(animateTaskId);
	}

	public void stopFrameTask(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getNextFrameTaskId());
		doNextFrame = true;
	}

	public boolean hasPlayedDesign(Design design) {
		for (Design _design : getDesignsPlayed()) {
			if (design.getMin().equals(_design.getMin()))
				return true;
		}

		return false;
	}

	public Design getRandomDesign() {
		int designCount = getGameDesigns().size() - 1;

		int ndx = RandomUtils.randomInt(0, designCount);
		Design design = getGameDesigns().get(ndx);
		for (int i = 0; i < designCount; i++) {
			ndx = RandomUtils.randomInt(0, designCount);
			design = getGameDesigns().get(ndx);
			if (!hasPlayedDesign(design))
				break;
		}

		return design;
	}

	public Design getRandomLobbyDesign() {
		int designCount = getGameDesigns().size() - 1;

		int ndx = RandomUtils.randomInt(0, designCount);
		Design design = getGameDesigns().get(ndx);
		for (int i = 0; i < designCount; i++) {
			ndx = RandomUtils.randomInt(0, designCount);
			design = getGameDesigns().get(ndx);
			if (!getLobbyDesign().getMin().equals(design.getMin()))
				break;
		}

		return design;
	}

	@Getter
	@AllArgsConstructor
	public static class Design {
		String word;
		Location min;
	}

	public void loadDesigns(Match match) {
		PixelDropArena arena = match.getArena();

		for (ProtectedRegion stackRegion : arena.getStackRegions()) {
			Location min = arena.worldedit().toLocation(stackRegion.getMinimumPoint());
			int count = min.getWorld().getHighestBlockYAt(min) + 64; // world starts at -64

			Location wordLoc = min.clone();
			for (int i = 0; i < count; i++) {
				Location designLoc = wordLoc.clone().add(0, i, 0);
				String word = getWord(designLoc.clone().subtract(1, 0, 0));
				Design design = new Design(word, designLoc.clone());

				gameDesigns.add(design);
			}
		}

		Collections.shuffle(gameDesigns);
	}

	public String getWord(Location location) {
		BlockState blockState = location.getBlock().getState();
		if (!(blockState instanceof Sign sign))
			throw new NexusException("PixelDrop could not parse sign at: " + StringUtils.getLocationString(location));

		String[] lines = sign.getLines();
		StringBuilder word = new StringBuilder();

		for (String line : lines) {
			if (!line.isEmpty())
				word.append(line.trim());
		}

		return word.toString().replaceAll("_", " ");
	}

	public void startWordTask(Match match) {
		String word = getRoundWord().replace("_", " ");
		String underscores = word.replaceAll("[a-zA-z0-9]", "_");
		AtomicReference<String> hint = new AtomicReference<>(underscores);

		wordTaskId = match.getTasks().repeat(0, TickTime.SECOND.x(2), () -> {
			long secondsElapsed = Duration.between(getRoundStart(), LocalDateTime.now()).getSeconds();
			if (secondsElapsed > 10) {
				int chance = 20 + (5 * guessed.size());
				if (RandomUtils.chanceOf(chance)) {
					String oldHint = hint.get();
					char letter = '-';
					int ndx = -1;

					// Try 5 times
					for (int i = 0; i < 5; i++) {
						int random = RandomUtils.randomInt(0, word.length() - 1);
						letter = oldHint.charAt(random);
						if (letter == '_') {
							ndx = oldHint.indexOf(letter, random);
							letter = word.charAt(ndx);
							break;
						}
					}

					if (ndx != -1) {
						char[] chars = oldHint.toCharArray();
						chars[ndx] = letter;
						String newHint = String.valueOf(chars);
						hint.set(newHint);
						if (newHint.equalsIgnoreCase(word)) {
							PixelDrop pixelDrop = match.getMechanic();
							pixelDrop.endTheRound(match);
						}
					}
				}
			}
			List<Minigamer> minigamers = match.getMinigamers();

			BossBar oldBossBar = bossBar;

			bossBar = new BossBarBuilder()
				.color(ColorType.PINK)
				.title("&2" + hint.get())
				.build();

			minigamers.forEach(minigamer -> {
				if (oldBossBar != null)
					match.hideBossBar(oldBossBar);

				match.showBossBar(bossBar);
			});
		});
	}

	public void stopWordTask(Match match) {
		match.getTasks().cancel(wordTaskId);
	}

	public void clearFloor(Match match) {
		PixelDropArena arena = match.getArena();
		List<Block> blocks = new ArrayList<>(worldedit().getBlocks(arena.getBoardRegion()));

		AtomicInteger taskId = new AtomicInteger();
		taskId.set(match.getTasks().repeat(0, 2, () -> {
			if (blocks.isEmpty())
				match.getTasks().cancel(taskId.get());

			for (int i = 0; i < 3; i++) {
				if (blocks.isEmpty())
					match.getTasks().cancel(taskId.get());

				Block block = RandomUtils.randomElement(blocks);
				blocks.remove(block);
				if (!Nullables.isNullOrAir(block))
					block.setType(Material.AIR);
			}
		}));
	}
}
