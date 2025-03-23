package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.DeathsStat;

public class DropperStatistics extends MatchStatistics implements DeathsStat {

	public static final MinigameStatistic LEVELS_PASSED = new MinigameStatistic("levels_passed", "Levels Passed");

	public DropperStatistics(Match match) {
		super(match);
	}
}
