package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void kill(Minigamer minigamer) {
		if (minigamer.isRespawning()) return;

		minigamer.clearState();
		if (minigamer.getLives() != 0) {
			minigamer.died();
			if (minigamer.getLives() == 0) {
				minigamer.setAlive(false);
				minigamer.toSpectate();
			} else {
				minigamer.respawn();
			}
		} else {
			minigamer.respawn();
		}

		super.kill(minigamer);
	}

}
