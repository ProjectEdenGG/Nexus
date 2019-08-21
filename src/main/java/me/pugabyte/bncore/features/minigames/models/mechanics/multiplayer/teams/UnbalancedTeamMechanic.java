package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class UnbalancedTeamMechanic extends TeamMechanic {

	@Override
	public List<Minigamer> balance(List<Minigamer> minigamers) {
		Arena arena = minigamers.get(0).getMatch().getArena();

		List<Team> teams = new ArrayList<>(arena.getTeams());

		int totalBalancePercentage = 0;
		for (Team team : teams) {
			totalBalancePercentage += team.getBalancePercentage();
		}

		if (totalBalancePercentage != 100) {
			BNCore.warn("The total balance percentage between all the teams on arena " + arena.getDisplayName() +
					" does not equal 100! Please check your configs.");
		}

		Collections.shuffle(minigamers);
		Collections.shuffle(teams);

		// TODO: Balance

		return minigamers;
	}

}
