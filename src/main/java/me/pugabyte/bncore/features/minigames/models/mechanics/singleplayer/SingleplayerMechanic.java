package me.pugabyte.bncore.features.minigames.models.mechanics.singleplayer;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleplayerMechanic extends Mechanic {

	@Override
	public List<Minigamer> balance(List<Minigamer> minigamers) {
		Arena arena = minigamers.get(0).getMatch().getArena();
		List<Team> teams = new ArrayList<>(arena.getTeams());

		minigamers.forEach(minigamer -> minigamer.setTeam(teams.get(0)));

		return minigamers;
	}

}
