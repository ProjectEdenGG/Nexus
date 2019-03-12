package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

import java.util.Collections;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void kill(Minigamer minigamer) {
		minigamer.setRespawning(true);
		minigamer.clearState();
		minigamer.teleport(minigamer.getMatch().getArena().getRespawnLocation());
		BNCore.runTaskLater(() -> {
			if (!minigamer.getMatch().isOver()) {
				minigamer.getTeam().spawn(Collections.singletonList(minigamer));
				minigamer.setRespawning(false);
			}
		}, (20 * 5));
	}
}
