package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class SpleggStatistics extends MatchStatistics {

	public static final MinigameStatistic EGGS_FIRED = new MinigameStatistic("eggs_fired", "Eggs Fired");
	public static final MinigameStatistic BLOCKS_BROKEN = new MinigameStatistic("blocks_broken", "Blocks Broken");

	public SpleggStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
