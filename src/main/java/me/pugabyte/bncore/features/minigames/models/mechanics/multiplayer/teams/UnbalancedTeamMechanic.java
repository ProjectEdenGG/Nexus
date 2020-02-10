package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class UnbalancedTeamMechanic extends TeamMechanic {

	@Override
	public void balance(List<Minigamer> minigamers) {
		if (minigamers.size() == 0)
			return;

		Match match = minigamers.get(0).getMatch();
		Arena arena = match.getArena();
		List<Team> teams = new ArrayList<>(arena.getTeams());

		int totalBalancePercentage = 0;
		for (Team team : teams)
			totalBalancePercentage += team.getBalancePercentage();

		if (totalBalancePercentage != 100) {
			BNCore.warn("The total balance percentage between all the teams on arena " + arena.getDisplayName() +
					" does not equal 100! Please check your configs.");
		}

		Collections.shuffle(minigamers);

		int required = 0;
		for (Team team : teams) required += team.getMinPlayers();

		if (minigamers.size() < required) {
			String message = "Not enough players to meet team requirements!";
			BNCore.severe(message);
			match.broadcast("&c" + message);
			match.end();
			return;
		}

		for (Team team : teams)
			while (team.getMinigamers(match).size() < team.getMinPlayers()) {
				Minigamer minigamer = Utils.getRandomElement(match.getUnassignedPlayers());
				if (minigamer.getTeam() == null)
					minigamer.setTeam(team);
			}

		playerLoop:
		for (Minigamer minigamer : minigamers)
			while (minigamer.getTeam() == null) {
				Collections.shuffle(teams);
				for (Team team : teams)
					if (Utils.chanceOf(team.getBalancePercentage())) {
						minigamer.setTeam(team);
						continue playerLoop;
					}
			}
	}

}
