package gg.projecteden.nexus.features.minigames.models.statistics.models.generics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;

public class PVPStats extends MatchStatistics implements DeathsStat, KillsStat {
	public PVPStats(Match match) {
		super(match);
	}
}
