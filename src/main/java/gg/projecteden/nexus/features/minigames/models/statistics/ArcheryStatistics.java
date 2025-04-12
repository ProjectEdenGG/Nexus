package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class ArcheryStatistics extends MatchStatistics {

	public static final MinigameStatistic ARROWS_FIRED = new MinigameStatistic("arrows_fired", "Arrows Fired");
	public static final MinigameStatistic TARGETS_HIT = new MinigameStatistic("targets_hit", "Targets Hit");

	public ArcheryStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
