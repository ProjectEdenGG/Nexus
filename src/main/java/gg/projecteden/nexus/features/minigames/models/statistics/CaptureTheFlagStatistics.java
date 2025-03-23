package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class CaptureTheFlagStatistics extends FlagRushStatistics {

	public static final MinigameStatistic FLAG_RETURNS = new MinigameStatistic("flag_returns", "Flag Returns");

	public CaptureTheFlagStatistics(Match match) {
		super(match);
	}
}
