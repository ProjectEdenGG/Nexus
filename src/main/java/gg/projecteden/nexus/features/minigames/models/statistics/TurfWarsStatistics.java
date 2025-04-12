package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class TurfWarsStatistics extends PVPStats {

	public static final MinigameStatistic BLOCKS_BROKEN = new MinigameStatistic("blocks_broken", "Blocks Broken");

	public TurfWarsStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
