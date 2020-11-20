package me.pugabyte.nexus.features.minigames.models.matchdata;

import me.pugabyte.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;

@MatchDataFor(CheckpointMechanic.class)
public class CheckpointMatchData extends CheckpointData {

	public CheckpointMatchData(Match match) {
		super(match);
	}

}
