package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic.Formula;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class ArcheryStatistics extends MatchStatistics {

	public static final MinigameStatistic ARROWS_FIRED = new MinigameStatistic("arrows_fired", "Arrows Fired");
	public static final MinigameStatistic TARGETS_HIT = new MinigameStatistic("targets_hit", "Targets Hit");
	public static final MinigameStatistic ACCURACY = new FormulaStatistic("accuracy", "Accuracy",
		Formula.of(TARGETS_HIT).divide(ARROWS_FIRED, Formula.constant(0)).multiply(Formula.constant(100))) {
		@Override
		public Object format(double score) {
			return super.format(score) + "%";
		}
	};

	public ArcheryStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
