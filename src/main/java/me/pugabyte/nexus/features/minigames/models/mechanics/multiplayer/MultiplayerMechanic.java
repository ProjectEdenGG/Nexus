package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.Mechanic;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (event.getMatch().isEnded())
			return;

		Minigamer victim = event.getMinigamer();
		if (victim.isRespawning()) return;

		victim.clearState();
		if (victim.getLives() != 0) {
			victim.died();
			if (victim.getLives() == 0) {
				victim.setAlive(false);
				if (victim.getMatch().getArena().getSpectateLocation() == null)
					victim.quit();
				else
					victim.toSpectate();
			} else {
				victim.respawn();
			}
		} else {
			victim.respawn();
		}

		super.onDeath(event);
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

	@Override
	public void begin(MatchBeginEvent event) {
		super.begin(event);

		Match match = event.getMatch();
		Arena arena = match.getArena();

		if (arena.getTurnTime() > 0)
			nextTurn(match);
	}

	public boolean showTurnTimerInChat() {
		return true;
	}

	public boolean shuffleTurnList() {
		return false;
	}

	abstract public void nextTurn(Match match);

}
