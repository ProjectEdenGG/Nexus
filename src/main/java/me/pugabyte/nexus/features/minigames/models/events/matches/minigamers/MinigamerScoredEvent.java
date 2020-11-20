package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.minigames.models.Minigamer;

public class MinigamerScoredEvent extends MinigamerEvent {
	@Getter
	@Setter
	private int amount;

	public MinigamerScoredEvent(Minigamer minigamer, int amount) {
		super(minigamer);
		this.amount = amount;
	}

}
