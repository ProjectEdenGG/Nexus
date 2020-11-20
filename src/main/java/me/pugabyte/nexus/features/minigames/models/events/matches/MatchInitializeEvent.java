package me.pugabyte.nexus.features.minigames.models.events.matches;

import me.pugabyte.nexus.features.minigames.models.Match;

public class MatchInitializeEvent extends MatchEvent {

	public MatchInitializeEvent(final Match match) {
		super(match);
	}

}
