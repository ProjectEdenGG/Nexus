package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class PaintballStatistics extends PVPStats {

	public static final MinigameStatistic PAINTBALLS_THROWN = new MinigameStatistic("paintballs_thrown", "Paintballs Thrown");

	public PaintballStatistics(Match match) {
		super(match);
	}
}
