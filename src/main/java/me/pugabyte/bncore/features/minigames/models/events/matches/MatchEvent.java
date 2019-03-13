package me.pugabyte.bncore.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.events.MinigameEvent;

public class MatchEvent extends MinigameEvent {
	@NonNull
	@Getter
	private Match match;

	public MatchEvent(Match match) {
		super(match.getArena());
		this.match = match;
	}

}
