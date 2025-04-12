package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class FallingBlocksStatistics extends PVPStats {

	public static final MinigameStatistic POWER_UPS_COLLECTED = new MinigameStatistic("power_ups_collected", "Power Ups Collected");
	public static final MinigameStatistic TIMES_REACHED_TOP = new MinigameStatistic("times_reached_top", "Times Reached Top");

	public FallingBlocksStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
