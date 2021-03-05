package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class TeamDeathmatch extends BalancedTeamMechanic {

	@Override
	public String getName() {
		return "Team Deathmatch";
	}

	@Override
	public String getDescription() {
		return "Kill the other team";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.LEATHER_HELMET);
	}

	@Override
	public boolean useAlternativeRegen() {
		return true;
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (event.getAttacker() != null) {
			event.getAttacker().scored();
			event.getAttacker().getMatch().scored(event.getAttacker().getTeam());
		}
		super.onDeath(event);
	}

}
