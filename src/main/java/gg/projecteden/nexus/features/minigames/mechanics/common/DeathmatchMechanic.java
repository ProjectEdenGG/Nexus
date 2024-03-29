package gg.projecteden.nexus.features.minigames.mechanics.common;

import gg.projecteden.nexus.features.minigames.models.RegenType;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import org.jetbrains.annotations.NotNull;

public abstract class DeathmatchMechanic extends TeamMechanic {
	@Override
	public RegenType getRegenType() {
		return RegenType.TIER_4;
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		if (event.getAttacker() != null) {
			event.getAttacker().scored();
			event.getAttacker().getMatch().scored(event.getAttacker().getTeam());
		}
		super.onDeath(event);
	}
}
