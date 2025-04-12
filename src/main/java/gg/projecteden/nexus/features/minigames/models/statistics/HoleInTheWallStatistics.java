package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class HoleInTheWallStatistics extends MatchStatistics {

	public static final MinigameStatistic WALLS_PASSED = new MinigameStatistic("walls_passed", "Walls Passed");

	public HoleInTheWallStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
