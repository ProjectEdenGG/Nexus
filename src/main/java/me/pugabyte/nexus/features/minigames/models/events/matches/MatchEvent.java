package me.pugabyte.nexus.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.events.MinigameEvent;

public abstract class MatchEvent extends MinigameEvent {
	@Getter
	@NonNull
	protected final Match match;

	public MatchEvent(Match match) {
		super(match.getArena());
		this.match = match;
	}

}
