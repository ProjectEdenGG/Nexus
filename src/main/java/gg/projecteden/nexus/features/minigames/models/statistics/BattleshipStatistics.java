package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class BattleshipStatistics extends MatchStatistics {

	public static final MinigameStatistic SHOTS_FIRED = new MinigameStatistic("shots_fired", "Shots Fired");
	public static final MinigameStatistic HITS = new MinigameStatistic("hits", "Hits");
	public static final MinigameStatistic SHIPS_SANK = new MinigameStatistic("ships_sank", "Ships Sank");

	public BattleshipStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
