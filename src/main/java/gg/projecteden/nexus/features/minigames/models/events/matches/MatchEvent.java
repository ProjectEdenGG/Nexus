package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.events.MinigameEvent;
import lombok.Getter;
import lombok.NonNull;

public abstract class MatchEvent extends MinigameEvent {
	@Getter
	@NonNull
	protected final Match match;

	public MatchEvent(Match match) {
		super(match.getArena());
		this.match = match;
	}

}
