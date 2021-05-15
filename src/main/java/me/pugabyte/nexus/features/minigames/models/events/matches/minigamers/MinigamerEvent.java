package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEvent;

public class MinigamerEvent extends MatchEvent {
	@Getter
	@NonNull
	protected final Minigamer minigamer;

	public MinigamerEvent(Minigamer minigamer) {
		super(minigamer.getMatch());
		this.minigamer = minigamer;
	}

}
