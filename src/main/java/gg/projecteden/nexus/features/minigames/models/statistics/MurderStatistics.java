package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class MurderStatistics extends MatchStatistics {

	public static final MinigameStatistic INNOCENT_WINS = new MinigameStatistic("innocent_wins", "Innocent Wins");
	public static final MinigameStatistic MURDERER_WINS = new MinigameStatistic("murderer_wins", "Murderer Wins");
	public static final MinigameStatistic MURDERER_KILLS = new MinigameStatistic("murderer_kills", "Murderer Kills");
	public static final MinigameStatistic GUNNER_SHUTDOWNS = new MinigameStatistic("gunner_shutdowns", "Gunner Shutdowns");

	public MurderStatistics(Match match) {
		super(match);
	}
}
