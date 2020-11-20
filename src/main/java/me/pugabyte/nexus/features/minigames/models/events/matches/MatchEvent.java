package me.pugabyte.nexus.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.events.MinigameEvent;

public class MatchEvent extends MinigameEvent {
	@Getter
	@NonNull
	private Match match;

	public MatchEvent(Match match) {
		super(match.getArena());
		this.match = match;
	}

}
