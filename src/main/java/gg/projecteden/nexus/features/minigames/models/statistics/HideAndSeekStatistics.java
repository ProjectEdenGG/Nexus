package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class HideAndSeekStatistics extends PVPStats {

	public static final MinigameStatistic HIDER_WINS = new MinigameStatistic("hider_wins", "Hider Wins");

	public HideAndSeekStatistics(Match match) {
		super(match);
	}
}
