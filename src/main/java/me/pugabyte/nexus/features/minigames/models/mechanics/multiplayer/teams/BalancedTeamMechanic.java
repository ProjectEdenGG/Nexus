package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams;

import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BalancedTeamMechanic extends TeamMechanic {

	@Override
	public void balance(List<Minigamer> minigamers) {
		Arena arena = minigamers.get(0).getMatch().getArena();

		List<Team> teams = new ArrayList<>(arena.getTeams());
		Collections.shuffle(minigamers);
		Collections.shuffle(teams);

		for (Minigamer minigamer : minigamers) {
			if (minigamer.getTeam() != null) continue;
			minigamer.setTeam(getSmallestTeam(minigamers, teams));
		}
	}

	public void rebalance(Match match, List<Minigamer> minigamers) {
		if (isTeamGame()) {
			List<Team> teams = new ArrayList<>(match.getArena().getTeams());
			// TODO: Rebalancing
		}
	}

}
