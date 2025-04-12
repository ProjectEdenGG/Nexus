package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class OneInTheQuiverStatistics extends PVPStats {

	public static final MinigameStatistic ARROW_KILLS = new MinigameStatistic("arrow_kills", "Arrow Kills");

	public OneInTheQuiverStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
