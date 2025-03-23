package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class PixelDropStatistics extends MatchStatistics {

	public static final MinigameStatistic CORRECT_GUESSES = new MinigameStatistic("correct_guesses", "Correct Guesses");

	public PixelDropStatistics(Match match) {
		super(match);
	}
}
