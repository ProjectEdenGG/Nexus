package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.PixelDrop;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.arenas.PixelDropArena;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@MatchDataFor(PixelDrop.class)
public class PixelDropMatchData extends MatchData {
//	private List<Minigamer> guessed = new ArrayList<>();
//	@Accessors(fluent = true)
//	private boolean canGuess;
//	private int totalFinished;

	//	private List<Integer> designsPlayed = new ArrayList<>();
//	private Map<String, Material> designMap = new HashMap<>();
//	private int designY;
	private int designCount;

//	private int currentRound;
//	private long roundStart;
//	private int timeLeft;
//	private int roundCountdownId;
//	private boolean roundOver;

	private Map<String, Material> lobbyDesignMap = new HashMap<>();
	private List<String> keys = new ArrayList<>();
	private int nextFrameTaskId;
	private boolean doNextFrame;
	private boolean animateLobby;
	private int lobbyDesign;
	private int animateLobbyId;

	public PixelDropMatchData(Match match) {
		super(match);
	}


	public void startLobbyAnimation(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		PixelDropArena arena = match.getArena();
		matchData.setLobbyDesign(0);
		countDesigns(match);

		int animateTaskId = match.getTasks().repeat(0, 2 * 20, () -> {
			if (match.isEnded() || match.isStarted() || !doNextFrame)
				return;

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
			Vector designMin = designsRegion.getMinimumPoint().subtract(0, 1, 0).add(0, design, 0);
			// Get min point of paste region
			Vector pasteMin = lobbyAnimationRegion.getMinimumPoint();

			// Builds the map
			for (int x = 0; x < 15; x++) {
				for (int z = 0; z < 15; z++) {
					Block block = WGUtils.toLocation(designMin.add(x, 0, z)).getBlock();
					String key = x + "_" + z;
					lobbyDesignMap.put(key, block.getType());
					keys.add(key);
				}
			}

			// Random Paste
			matchData.setDoNextFrame(false);
			int nextFrameTaskId = match.getTasks().repeat(0, 2, () -> {
				if (keys.size() == 0) {
					stopFrameTask(match);
					return;
				}

				String key = Utils.getRandomElement(keys);
				keys.remove(key);
				String[] xz = key.split("_");
				int x = Integer.parseInt(xz[0]);
				int z = Integer.parseInt(xz[1]);

				Material material = lobbyDesignMap.get(x + "_" + z);
				WGUtils.toLocation(pasteMin.add(x, 0, z)).getBlock().setType(material);
			});
			matchData.setNextFrameTaskId(nextFrameTaskId);
		});
		matchData.setAnimateLobbyId(animateTaskId);
	}

	public void stopFrameTask(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getNextFrameTaskId());
		matchData.setDoNextFrame(true);
	}

	public void countDesigns(Match match) {
		PixelDropArena arena = match.getArena();
		Region designsRegion = arena.getDesignRegion();

		int area = designsRegion.getArea();
		EditSession editSession = WEUtils.getEditSession();
		int airCount = editSession.countBlocks(designsRegion, Collections.singleton(new BaseBlock(Material.AIR.getId())));
		int blocksCount = area - airCount;

		PixelDropMatchData matchData = match.getMatchData();
		int totalDesigns = blocksCount / 225;
		matchData.setDesignCount(totalDesigns);
	}
}
