package gg.projecteden.nexus.features.minigames.models.statistics.models.generics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;

public class PVPStats extends MatchStatistics implements DeathsStat, KillsStat {
	public PVPStats(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
