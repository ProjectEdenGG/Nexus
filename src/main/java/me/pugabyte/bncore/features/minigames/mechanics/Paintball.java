package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public final class Paintball extends BalancedTeamMechanic {

	@Override
	public String getName() {
		return "Paintball";
	}

	@Override
	public String getDescription() {
		return "Shoot players";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.SNOW_BALL);
	}

	@Override
	public void onDeath(Minigamer victim, Minigamer killer) {
		super.onDeath(victim, killer);
		killer.scored();
		killer.getMatch().scored(killer.getTeam());
	}

	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		if (event.getHitEntity() == null) return;
		if (!(event.getEntity() instanceof Snowball)) return;
		if (!(event.getHitEntity() instanceof Player)) return;
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		Minigamer victim = PlayerManager.get((Player) event.getHitEntity());
		Minigamer attacker = PlayerManager.get((Player) event.getEntity().getShooter());
		if (victim.isPlaying(this) && attacker.isPlaying(this))
			kill(victim, attacker);
	}

	// TODO:
	// Paint mechanic (armour, blocks)
	// Guns
	// Different objects to throw (enderpearl/snowball/armour stand with colored blocks)

}
