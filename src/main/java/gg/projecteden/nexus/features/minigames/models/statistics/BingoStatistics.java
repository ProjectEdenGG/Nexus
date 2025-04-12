package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.DeathsStat;

public class BingoStatistics extends MatchStatistics implements DeathsStat {

	public static final MinigameStatistic CHALLENGES_COMPLETED = new MinigameStatistic("challenges_completed", "Challenges Completed");
	public static final MinigameStatistic BINGOS = new MinigameStatistic("bingos", "Bingos");

	public BingoStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
