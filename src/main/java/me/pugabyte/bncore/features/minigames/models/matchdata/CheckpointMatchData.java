package me.pugabyte.bncore.features.minigames.models.matchdata;

import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;

@MatchDataFor(CheckpointMechanic.class)
public class CheckpointMatchData extends CheckpointData {

	public CheckpointMatchData(Match match) {
		super(match);
	}

}
