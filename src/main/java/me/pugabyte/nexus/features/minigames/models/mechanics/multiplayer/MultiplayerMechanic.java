package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.utils.RandomUtils;

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

	public int getMultiplier(Match match, Minigamer minigamer) {
		// int winningScore = getWinningScore(match.getMinigamers().stream().map(Minigamer::getScore).collect(Collectors.toList()));
		// ^ feels like a computational waste to do this
		int winningScore = match.getWinningScore();
		if (minigamer.getScore() <= 0 || winningScore <= 0)
			return 0;
		return winningScore - Math.min(winningScore, minigamer.getScore()) + 1;
	}

	public void giveRewards(Match match) {
		match.getMinigamers().forEach(minigamer -> {
			PerkOwner perkOwner = PerkOwner.service.get(minigamer.getPlayer());
			// max of 1 in 50 chance of getting a reward (dependant on score)
			int multiplier = getMultiplier(match, minigamer);
			if (multiplier == 0)
				return;
			if (RandomUtils.randomInt(1, 20 * multiplier) == 1)
				perkOwner.reward(match.getArena());
		});
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		giveRewards(event.getMatch());
	}
}
