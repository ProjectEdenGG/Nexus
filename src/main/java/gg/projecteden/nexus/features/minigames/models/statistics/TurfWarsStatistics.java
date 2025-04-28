package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic.Formula;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class TurfWarsStatistics extends PVPStats {

	public static final MinigameStatistic BLOCKS_BROKEN = new MinigameStatistic("blocks_broken", "Blocks Broken");
	public static final MinigameStatistic ARROWS_FIRED = new MinigameStatistic("arrows_fired", "Arrows Fired");
	public static final MinigameStatistic ACCURACY = new FormulaStatistic("accuracy", "Accuracy",
		Formula.of(KILLS).add(BLOCKS_BROKEN).divide(ARROWS_FIRED, Formula.constant(0)).multiply(Formula.constant(100))) {
		@Override
		public Object format(double score) {
			return super.format(score) + "%";
		}
	};

	public TurfWarsStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
