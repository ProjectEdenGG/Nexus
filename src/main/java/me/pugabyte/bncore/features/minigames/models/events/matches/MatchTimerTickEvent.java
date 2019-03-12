package me.pugabyte.bncore.features.minigames.models.events.matches;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.models.Match;

public class MatchTimerTickEvent extends MatchEvent {
	@Getter
	private int time;

	public MatchTimerTickEvent(final Match match, int time) {
		super(match);
		this.time = time;
	}
}
