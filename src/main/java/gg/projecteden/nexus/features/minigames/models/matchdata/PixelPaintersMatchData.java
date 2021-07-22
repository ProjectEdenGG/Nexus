package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.CuboidRegion;
import gg.projecteden.nexus.features.minigames.mechanics.PixelPainters;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;
import lombok.experimental.Accessors;

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
	private int roundCountdownId;
	private int timeLeft;
	private boolean animateLobby;
	private int lobbyDesign;
	private int animateLobbyId;

	public PixelPaintersMatchData(Match match) {
		super(match);
	}
}
