package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.CuboidRegion;
import gg.projecteden.nexus.features.minigames.mechanics.TurfWars;
import gg.projecteden.nexus.features.minigames.mechanics.TurfWars.FloorRow;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@MatchDataFor(TurfWars.class)
public class TurfWarsMatchData extends MatchData {

	private State state = State.BUILD;
	private int time;
	private int phase;
	private List<FloorRow> rows = new ArrayList<>();

	private CuboidRegion team1Region;
	private CuboidRegion team2Region;

	public TurfWarsMatchData(Match match) {
		super(match);
	}

	public int getFloorWorth() {
		return this.phase / 2;
	}

	public enum State {
		BUILD,
		FIGHT;

		public String getTitle() {
			return "&e&l" + StringUtils.camelCase(name());
		}
	}

}
