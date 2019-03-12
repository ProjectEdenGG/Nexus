package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class BalancedTeamMechanic extends TeamMechanic {

	@Override
	public List<Minigamer> balance(List<Minigamer> minigamers) {
		Arena arena = minigamers.get(0).getMatch().getArena();

		List<Team> teams = new ArrayList<>(arena.getTeams());
		Collections.shuffle(minigamers);
		Collections.shuffle(teams);

		while (minigamers.stream().anyMatch(minigamer -> minigamer.getTeam() == null)) {
			Optional<Minigamer> next = minigamers.stream().filter(minigamer -> minigamer.getTeam() == null).findAny();
			next.ifPresent(minigamer -> minigamer.setTeam(getSmallestTeam(minigamers, teams)));
		}

		return minigamers;
	}

	public List<Minigamer> rebalance(Match match, List<Minigamer> minigamers) {
		if (isTeamGame()) {
			List<Team> teams = new ArrayList<>(match.getArena().getTeams());
			// TODO: Rebalancing
		}
		return minigamers;
	}
}
