package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Data;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.mechanics.PixelPainters;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;

import java.util.ArrayList;
import java.util.List;

@Data
@MatchDataFor(PixelPainters.class)
public class PixelPaintersMatchData extends MatchData {
	private List<Minigamer> checked = new ArrayList<>();
	private List<Integer> designsPlayed = new ArrayList<>();
	@Accessors(fluent = true)
	private boolean canCheck;
	private boolean roundOver;
	private int currentRound;
	private int designCount;
	private CuboidRegion designRegion;
	private long roundStart;
	private int totalFinished;
	private int roundCountdownID;
	private int timeLeft;
	private boolean animateLobby;
	private int lobbyDesign;
	private int animateLobbyID;


	public PixelPaintersMatchData(Match match) {
		super(match);
	}
}
