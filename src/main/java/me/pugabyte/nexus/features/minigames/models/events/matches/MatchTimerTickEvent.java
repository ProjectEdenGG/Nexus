package me.pugabyte.nexus.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Match;

public class MatchTimerTickEvent extends MatchEvent {
	@Getter
	@NonNull
	private int time;

	public MatchTimerTickEvent(final Match match, int time) {
		super(match);
		this.time = time;
	}

}
