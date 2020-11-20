package me.pugabyte.nexus.features.minigames.models.events.matches;

import me.pugabyte.nexus.features.minigames.models.Match;

public class MatchStartEvent extends MatchEvent {

	public MatchStartEvent(final Match match) {
		super(match);
	}

}
