package me.pugabyte.bncore.features.minigames.models.events.matches.minigamers;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.models.Minigamer;

public class MinigamerDeathEvent extends MinigamerEvent {
	@Getter
	private Minigamer attacker;

	public MinigamerDeathEvent(Minigamer victim) {
		super(victim);
	}

	public MinigamerDeathEvent(Minigamer victim, Minigamer attacker) {
		super(victim);
		this.attacker = attacker;
	}

}
