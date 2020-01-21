package me.pugabyte.bncore.features.minigames.models.events.matches;

import me.pugabyte.bncore.features.minigames.models.Match;

public class MatchInitializeEvent extends MatchEvent {

	public MatchInitializeEvent(final Match match) {
		super(match);
	}

}
