package me.pugabyte.nexus.features.minigames.mechanics.common;

import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;

public abstract class DeathmatchMechanic extends TeamMechanic {
	@Override
	public boolean useAlternativeRegen() {
		return true;
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (event.getAttacker() != null) {
			event.getAttacker().scored();
			event.getAttacker().getMatch().scored(event.getAttacker().getTeam());
		}
		super.onDeath(event);
	}
}
