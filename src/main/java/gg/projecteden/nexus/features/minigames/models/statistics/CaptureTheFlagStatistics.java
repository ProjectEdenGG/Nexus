package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class CaptureTheFlagStatistics extends FlagRushStatistics {

	public static final MinigameStatistic FLAG_RETURNS = new MinigameStatistic("flag_returns", "Flag Returns");

	public CaptureTheFlagStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
