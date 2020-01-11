package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TeamDeathmatch extends BalancedTeamMechanic {

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
	public void onDeath(Minigamer victim, Minigamer killer) {
		super.onDeath(victim, killer);
		killer.scored();
		killer.getMatch().scored(killer.getTeam());
	}

}
