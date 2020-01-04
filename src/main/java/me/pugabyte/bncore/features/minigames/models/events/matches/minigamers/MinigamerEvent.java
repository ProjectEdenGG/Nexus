package me.pugabyte.bncore.features.minigames.models.events.matches.minigamers;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEvent;

public class MinigamerEvent extends MatchEvent {
	@Getter
	@NonNull
	private Minigamer minigamer;

	public MinigamerEvent(Minigamer minigamer) {
		super(minigamer.getMatch());
		this.minigamer = minigamer;
	}

}
