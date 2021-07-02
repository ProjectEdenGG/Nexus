package me.pugabyte.nexus.features.minigames.models.matchdata;

import me.pugabyte.nexus.features.minigames.mechanics.UncivilEngineers;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;

@MatchDataFor(UncivilEngineers.class)
public class UncivilEngineersMatchData extends CheckpointData {

	public UncivilEngineersMatchData(Match match) {
		super(match);
	}

}
