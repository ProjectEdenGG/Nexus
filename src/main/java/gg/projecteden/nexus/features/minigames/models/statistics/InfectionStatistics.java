package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class InfectionStatistics extends PVPStats {

	public static final MinigameStatistic HUMAN_WINS = new MinigameStatistic("human_wins", "Human Wins");

	public InfectionStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
