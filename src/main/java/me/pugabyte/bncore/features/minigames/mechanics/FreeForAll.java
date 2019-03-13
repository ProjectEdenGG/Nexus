package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;

public class FreeForAll extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Free For All";
	}

	@Override
	public String getDescription() {
		return "Kill everyone!";
	}

	@Override
	public void onDeath(Minigamer victim, Minigamer killer) {
		super.onDeath(victim, killer);
		killer.scored();
	}

}
