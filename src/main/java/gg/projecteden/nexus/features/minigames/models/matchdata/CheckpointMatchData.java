package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;

@MatchDataFor(CheckpointMechanic.class)
public class CheckpointMatchData extends CheckpointData {

	public CheckpointMatchData(Match match) {
		super(match);
	}

}
