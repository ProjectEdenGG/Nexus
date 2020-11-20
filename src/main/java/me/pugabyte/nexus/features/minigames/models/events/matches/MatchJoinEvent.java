package me.pugabyte.nexus.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;

public class MatchJoinEvent extends MatchEvent {
	@Getter
	@NonNull
	private Minigamer minigamer;

	public MatchJoinEvent(Match match, Minigamer minigamer) {
		super(match);
		this.minigamer = minigamer;
	}

}
