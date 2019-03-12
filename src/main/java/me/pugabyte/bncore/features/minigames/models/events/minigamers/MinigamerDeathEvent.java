package me.pugabyte.bncore.features.minigames.models.events.minigamers;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;

public class MinigamerDeathEvent extends MinigamerEvent {
	@Getter
	private Minigamer attacker;

	public MinigamerDeathEvent(Match match, Minigamer victim) {
		super(match, victim);
	}

	public MinigamerDeathEvent(Match match, Minigamer victim, Minigamer attacker) {
		super(match, victim);
		this.attacker = attacker;
	}
}
