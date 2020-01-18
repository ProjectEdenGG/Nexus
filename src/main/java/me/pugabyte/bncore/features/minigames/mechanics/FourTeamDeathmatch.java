package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class FourTeamDeathmatch extends BalancedTeamMechanic {

	@Override
	public String getName() {
		return "Four Team Deathmatch";
	}

	@Override
	public String getDescription() {
		return "Kill the other teams";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.DIAMOND_SWORD);
	}

	@Override
	public void onDeath(Minigamer victim, Minigamer killer) {
		super.onDeath(victim, killer);
		killer.scored();
		killer.getMatch().scored(killer.getTeam());
	}

}
