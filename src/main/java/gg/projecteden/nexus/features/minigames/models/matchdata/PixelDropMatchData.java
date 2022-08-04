package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.mechanics.PixelDrop;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.arenas.PixelDropArena;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.utils.BossBarBuilder;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Data
@MatchDataFor(PixelDrop.class)
public class PixelDropMatchData extends MatchData {
	private List<Minigamer> guessed = new ArrayList<>();
	@Accessors(fluent = true)

	private List<String> designWords = new ArrayList<>();
	private List<Integer> designsPlayed = new ArrayList<>();
	private Map<String, Block> designMap = new HashMap<>();
	private List<String> designKeys = new ArrayList<>();
	private int design;
	private int designCount;
	private int designTaskId;

	private String roundWord;
	private BossBar guessingBossBar = null;
	private BossBar guessedBossBar = null;
	private int wordTaskId;
	private int currentRound;
	private LocalDateTime roundStart;
	private long timeLeft;
	private int roundCountdownId;
	private boolean roundOver;

	private Map<String, Block> lobbyDesignMap = new HashMap<>();
	private List<String> lobbyKeys = new ArrayList<>();
	private int nextFrameTaskId;
	private boolean doNextFrame;
	private boolean animateLobby;
	private int lobbyDesign;
	private int animateLobbyId;

	public PixelDropMatchData(Match match) {
		super(match);
	}

	public void setupGame(Match match) {
		countDesigns(match);
		setupDesignWords(match);
		setCurrentRound(0);
		setTimeLeft(0);
		clearFloor(match);
	}

	public void endRound() {
		setRoundOver(true);
		setTimeLeft(0);
		if (guessingBossBar != null)
			match.hideBossBar(guessingBossBar);
	}

	public void resetRound() {
		getDesignMap().clear();
		getDesignKeys().clear();
		getGuessed().clear();
	}

	public void setupRound(Match match) {
		setRoundOver(false);
		setTimeLeft(0);
		match.getScoreboard().update();
		setCurrentRound(getCurrentRound() + 1);
		setRoundStart(LocalDateTime.now());
	}

	public void setDesign(int design) {
		this.design = design;
		roundWord = designWords.get(design - 1);
	}

	public void startLobbyAnimation(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		PixelDropArena arena = match.getArena();
		matchData.setLobbyDesign(0);
		countDesigns(match);
		doNextFrame = true;

		int animateTaskId = match.getTasks().repeat(0, TickTime.SECOND.x(2), () -> {
			if (match.isEnded() || match.isStarted() || !doNextFrame)
				return;
			doNextFrame = false;

			// Get Random Design
			Region designsRegion = arena.getDesignRegion();
			Region lobbyAnimationRegion = arena.getLobbyAnimationRegion();

			int designCount = matchData.getDesignCount();
			int design = RandomUtils.randomInt(1, designCount);
			for (int i = 0; i < designCount; i++) {
				design = RandomUtils.randomInt(1, designCount);
				if (matchData.getLobbyDesign() != design)
					break;
			}
			matchData.setLobbyDesign(design);

			// Get min point from current chosen design
			BlockVector3 designMin = designsRegion.getMinimumPoint().subtract(0, 1, 0).add(0, design, 0);
			// Get min point of paste region
			BlockVector3 pasteMin = lobbyAnimationRegion.getMinimumPoint();

			// Builds the map
			for (int x = 0; x < 15; x++) {
				for (int z = 0; z < 15; z++) {
					Block block = worldguard().toLocation(designMin.add(x, 0, z)).getBlock();
					String key = x + "_" + z;
					lobbyDesignMap.put(key, block);
					lobbyKeys.add(key);
				}
			}

			// Random Paste
			int nextFrameTaskId = match.getTasks().repeat(0, 2, () -> {
				for (int i = 0; i < 3; i++) {
					if (lobbyKeys.size() == 0) {
						stopFrameTask(match);
						return;
					}

					String key = RandomUtils.randomElement(lobbyKeys);
					lobbyKeys.remove(key);
					String[] xz = key.split("_");
					int x = Integer.parseInt(xz[0]);
					int z = Integer.parseInt(xz[1]);

					Block block = lobbyDesignMap.get(x + "_" + z);
					Location loc = worldguard().toLocation(pasteMin.add(x, 0, z));

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

	public void countDesigns(Match match) {
		PixelDropArena arena = match.getArena();
		PixelDropMatchData matchData = match.getMatchData();
		Location min = arena.worldedit().toLocation(arena.getDesignRegion().getMinimumPoint());
		int y = min.getWorld().getHighestBlockYAt(min);
		matchData.setDesignCount(y + 64);
	}

	public void setupDesignWords(Match match) {
		PixelDropArena arena = match.getArena();
		PixelDropMatchData matchData = match.getMatchData();
		Region designsRegion = arena.getDesignRegion();
		BlockVector3 minPoint = designsRegion.getMinimumPoint().subtract(1, 0, 0);
		int designCount = matchData.getDesignCount();

		for (int i = 0; i < designCount; i++) {
			Location signLoc = worldguard().toLocation(minPoint).add(0, i, 0);
			String word = getWord(signLoc);
			designWords.add(word);
		}
	}

	public String getWord(Location location) {
		BlockState blockState = location.getBlock().getState();
		if (!(blockState instanceof Sign sign))
			throw new NexusException("PixelDrop could not parse sign at: " + StringUtils.getLocationString(location));

		String[] lines = sign.getLines();
		StringBuilder word = new StringBuilder();
		for (String line : lines) {
			if (line.length() != 0)
				word.append(line);
		}
		word = new StringBuilder(word.toString().replaceAll("_", " "));
		return word.toString();
	}

	public void startWordTask(Match match) {
		String word = getRoundWord().replace("_", " ");
		String underscores = word.replaceAll("[a-zA-z]", "_");
		AtomicReference<String> hint = new AtomicReference<>(underscores);

		this.wordTaskId = match.getTasks().repeat(0, TickTime.SECOND.x(2), () -> {
			long secondsElapsed = Duration.between(getRoundStart(), LocalDateTime.now()).getSeconds();
			if (secondsElapsed > 10) {
				int chance = 15 + (5 * guessed.size());
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

			BossBar oldGuessingBossBar = guessingBossBar;
			BossBar oldGuessedBossBar = guessedBossBar;

			guessingBossBar = new BossBarBuilder()
				.color(ColorType.PINK)
				.title("&2" + hint.get())
				.build();

			guessedBossBar = new BossBarBuilder()
				.color(ColorType.PINK)
				.title("&7" + hint.get())
				.build();

			minigamers.forEach(minigamer -> {
				if (oldGuessingBossBar != null)
					match.hideBossBar(oldGuessingBossBar);
				if (oldGuessedBossBar != null)
					match.hideBossBar(oldGuessedBossBar);

				if (guessed.contains(minigamer))
					match.showBossBar(guessedBossBar);
				else
					match.showBossBar(guessingBossBar);
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
			if (blocks.size() == 0)
				match.getTasks().cancel(taskId.get());

			for (int i = 0; i < 3; i++) {
				if (blocks.size() == 0)
					match.getTasks().cancel(taskId.get());

				Block block = RandomUtils.randomElement(blocks);
				blocks.remove(block);
				if (!isNullOrAir(block))
					block.setType(Material.AIR);
			}
		}));
	}
}
