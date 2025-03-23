package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class PixelPaintersStatistics extends MatchStatistics {

	public static final MinigameStatistic BUILDS_COMPLETED = new MinigameStatistic("builds_completed", "Builds Completed");

	public PixelPaintersStatistics(Match match) {
		super(match);
	}
}
