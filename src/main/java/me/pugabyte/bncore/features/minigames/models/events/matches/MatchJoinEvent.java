package me.pugabyte.bncore.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;

public class MatchJoinEvent extends MatchEvent {
	@NonNull
	@Getter
	private Minigamer minigamer;

	public MatchJoinEvent(Match match, Minigamer minigamer) {
		super(match);
		this.minigamer = minigamer;
	}

}
