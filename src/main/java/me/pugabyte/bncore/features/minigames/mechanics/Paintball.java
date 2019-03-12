package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import static me.pugabyte.bncore.features.minigames.Minigames.getPlayerManager;

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
	public void onDeath(Minigamer victim, Minigamer killer) {
		super.onDeath(victim, killer);
		killer.scored();
		killer.getMatch().scored(killer.getTeam());
	}

	// TODO:
	// Paint mechanic (armour, blocks)
	// Guns
	// Different objects to throw (enderpearl/snowball/armour stand with colored blocks)

	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		if (event.getHitEntity() == null) return;
		if (!(event.getEntity() instanceof Snowball)) return;
		if (!(event.getHitEntity() instanceof Player)) return;
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		Minigamer victim = getPlayerManager().get((Player) event.getHitEntity());
		Minigamer attacker = getPlayerManager().get((Player) event.getEntity().getShooter());
		if (victim.isPlaying(Paintball.class) && attacker.isPlaying(Paintball.class)) {
			victim.getPlayer().damage(20, attacker.getPlayer());
		}
	}

}
