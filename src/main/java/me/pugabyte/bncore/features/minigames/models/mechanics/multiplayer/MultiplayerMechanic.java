package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void kill(Minigamer victim, Minigamer attacker) {
		if (victim.isRespawning()) return;

		victim.clearState();
		if (victim.getLives() != 0) {
			victim.died();
			if (victim.getLives() == 0) {
				victim.setAlive(false);
				victim.toSpectate();
			} else {
				victim.respawn();
			}
		} else {
			victim.respawn();
		}

		super.kill(victim, attacker);
	}

	@Override
	public void processJoin(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		if (match.isStarted()) {
			balance(minigamer);
			match.teleportIn(minigamer);
		} else {
			match.getArena().getLobby().join(minigamer);
		}
	}

}
