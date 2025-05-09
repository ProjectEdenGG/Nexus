package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic.Formula;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class PaintballStatistics extends PVPStats {

	public static final MinigameStatistic PAINTBALLS_THROWN = new MinigameStatistic("paintballs_thrown", "Paintballs Thrown");
	public static final MinigameStatistic ACCURACY = new FormulaStatistic("accuracy", "Accuracy",
		Formula.of(KILLS).divide(PAINTBALLS_THROWN, Formula.constant(0)).multiply(Formula.constant(100))) {
		@Override
		public Object format(double score) {
			return super.format(score) + "%";
		}
	};

	public PaintballStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
