package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.Data;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.mechanics.PixelDrop;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.arenas.PixelDropArena;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Data
@MatchDataFor(PixelDrop.class)
public class PixelDropMatchData extends MatchData {
	private List<Minigamer> guessed = new ArrayList<>();
	@Accessors(fluent = true)
	private boolean canGuess;

	private List<String> designWords = new ArrayList<>();
	private List<Integer> designsPlayed = new ArrayList<>();
	private Map<String, Block> designMap = new HashMap<>();
	private List<String> designKeys = new ArrayList<>();
	private int design;
	private int designCount;
	private int designTaskId;

	private String roundWord;
	private int wordTaskId;
	private int currentRound;
	private long roundStart;
	private int timeLeft;
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

	public void endRound(Match match) {
		canGuess(false);
		setRoundOver(true);
		setTimeLeft(0);
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
		setRoundStart(System.currentTimeMillis());
		canGuess(true);
	}

	public void setDesign(int design) {
		this.design = design;
		roundWord = designWords.get(design - 1);
	}

	public void setNewDesign(Match match) {

	}

	public void startLobbyAnimation(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		PixelDropArena arena = match.getArena();
		matchData.setLobbyDesign(0);
		countDesigns(match);
		doNextFrame = true;

		int animateTaskId = match.getTasks().repeat(0, Time.SECOND.x(2), () -> {
			if (match.isEnded() || match.isStarted() || !doNextFrame)
				return;
			doNextFrame = false;

			// Get Random Design
			Region designsRegion = arena.getDesignRegion();
			Region lobbyAnimationRegion = arena.getLobbyAnimationRegion();

			int designCount = matchData.getDesignCount();
			int design = Utils.randomInt(1, designCount);
			for (int i = 0; i < designCount; i++) {
				design = Utils.randomInt(1, designCount);
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
					Block block = WGUtils.toLocation(designMin.add(x, 0, z)).getBlock();
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

					String key = Utils.getRandomElement(lobbyKeys);
					lobbyKeys.remove(key);
					String[] xz = key.split("_");
					int x = Integer.parseInt(xz[0]);
					int z = Integer.parseInt(xz[1]);

					Block block = lobbyDesignMap.get(x + "_" + z);
					Location loc = WGUtils.toLocation(pasteMin.add(x, 0, z));
					loc.getBlock().setType(block.getType());
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
		Region designsRegion = arena.getDesignRegion();

		int area = designsRegion.getArea();
		EditSession editSession = WEUtils.getEditSession();
		int airCount = editSession.countBlocks(designsRegion, Collections.singleton(BlockTypes.AIR.getDefaultState().toBaseBlock()));
		int blocksCount = area - airCount;

		PixelDropMatchData matchData = match.getMatchData();
		int totalDesigns = blocksCount / 225;
		matchData.setDesignCount(totalDesigns);
	}

	public void setupDesignWords(Match match) {
		PixelDropArena arena = match.getArena();
		PixelDropMatchData matchData = match.getMatchData();
		Region designsRegion = arena.getDesignRegion();
		BlockVector3 minPoint = designsRegion.getMinimumPoint().subtract(1, 0, 0);
		int designCount = matchData.getDesignCount();

		for (int i = 0; i < designCount; i++) {
			Location signLoc = WGUtils.toLocation(minPoint).add(0, i, 0);
			String word = getWord(signLoc);
			designWords.add(word);
		}
	}

	public String getWord(Location location) {
		Sign sign = (Sign) location.getBlock().getState();
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

		this.wordTaskId = match.getTasks().repeat(0, Time.SECOND.x(2), () -> {
			long secondsElapsed = (System.currentTimeMillis() - getRoundStart()) / 1000;
			if (secondsElapsed > 10) {
				int chance = 15 + (5 * guessed.size());
				if (Utils.chanceOf(chance)) {
					String oldHint = hint.get();
					char letter = '-';
					int ndx = -1;

					// Try 5 times
					for (int i = 0; i < 5; i++) {
						int random = Utils.randomInt(0, word.length() - 1);
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
						hint.set(String.valueOf(chars));
					}
				}
			}
			List<Minigamer> minigamers = match.getMinigamers();
			minigamers.forEach(minigamer -> {
				if (!guessed.contains(minigamer))
					Utils.sendActionBar(minigamer.getPlayer(), hint.get(), 3 * 20, true);
				else
					Utils.sendActionBar(minigamer.getPlayer(), "&a" + word, 3 * 20, true);
			});
		});
	}

	public void stopWordTask(Match match) {
		match.getTasks().cancel(wordTaskId);
	}

	public void revealWord(Match match) {
		List<Minigamer> minigamers = match.getMinigamers();
		String word = getRoundWord().replaceAll("_", " ");
		minigamers.forEach(minigamer -> Utils.sendActionBar(minigamer.getPlayer(), "&a" + word, 40, true));
	}

	// TODO: Counter clockwise animation
	public void clearFloor(Match match) {
		PixelDropArena arena = match.getArena();
		WEUtils.fill(arena.getBoardRegion(), Material.AIR);
	}
}
