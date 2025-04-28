package gg.projecteden.nexus.features.minigames.models.statistics.models.generics;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic.Formula;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;

public class PVPStats extends MatchStatistics implements DeathsStat, KillsStat {
	public PVPStats(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}

	public static final MinigameStatistic KD_RATIO = new FormulaStatistic("kdr", "K/D Ratio", Formula.of(KILLS).divide(DEATHS, Formula.of(KILLS))) {
		@Override
		public Object format(double score) {
			return StringUtils.getDf().format(score);
		}
	};

}
