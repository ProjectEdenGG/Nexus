package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;

public class FlagRushStatistics extends PVPStats {

	public static final MinigameStatistic FLAG_PICK_UPS = new MinigameStatistic("flag_pick_ups", "Flag Pick Ups");
	public static final MinigameStatistic FLAG_CAPTURES = new MinigameStatistic("flag_captures", "Flag Captures");

	public FlagRushStatistics(MechanicType mechanic, Match match) {
		super(mechanic, match);
	}
}
