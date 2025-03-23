package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class ThimbleStatistics extends MatchStatistics {

	public static final MinigameStatistic JUMPS_SURVIVED = new MinigameStatistic("jumps_survived", "Jumps Survived");

	public ThimbleStatistics(Match match) {
		super(match);
	}
}
