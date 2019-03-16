package me.pugabyte.bncore.features.minigames.models.events.minigamers;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.MinigameEvent;

public class MinigamerEvent extends MinigameEvent {
	@Getter
	@NonNull
	private Match match;
	@Getter
	@NonNull
	private Minigamer minigamer;

	public MinigamerEvent(Match match, Minigamer minigamer) {
		super(match.getArena());
		this.match = match;
		this.minigamer = minigamer;
	}

}
