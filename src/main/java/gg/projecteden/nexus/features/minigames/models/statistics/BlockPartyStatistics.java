package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class BlockPartyStatistics extends MatchStatistics {

	public static final MinigameStatistic ROUNDS_SURVIVED = new MinigameStatistic( "rounds_survived", "Rounds Survived");
	public static final MinigameStatistic POWER_UPS_COLLECTED = new MinigameStatistic("power_ups_collected", "Power Ups Collected");
	public static final MinigameStatistic POWER_UPS_USED = new MinigameStatistic("power_ups_used", "Power Ups Used");

	public BlockPartyStatistics(Match match) {
		super(match);
	}
}
