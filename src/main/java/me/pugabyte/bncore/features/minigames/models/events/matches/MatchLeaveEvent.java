package me.pugabyte.bncore.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;

public class MatchLeaveEvent extends MatchEvent {
	@NonNull
	@Getter
	private Minigamer minigamer;

	public MatchLeaveEvent(Match match, Minigamer minigamer) {
		super(match);
		this.minigamer = minigamer;
	}
}
