package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.features.minigames.Minigames.getPlayerManager;

public final class OneInTheQuiver extends TeamlessMechanic {

	@Override
	public String getName() {
		return "One in the Quiver";
	}

	@Override
	public String getDescription() {
		return "Shoot players";
	}

	@Override
	public void onDeath(Minigamer victim, Minigamer killer) {
		super.onDeath(victim, killer);
		killer.scored();
		killer.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		if (event.getHitEntity() == null) return;
		if (!(event.getEntity() instanceof Arrow)) return;
		if (!(event.getHitEntity() instanceof Player)) return;
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		Minigamer victim = getPlayerManager().get((Player) event.getHitEntity());
		Minigamer attacker = getPlayerManager().get((Player) event.getEntity().getShooter());
		if (victim.isPlaying(OneInTheQuiver.class) && attacker.isPlaying(OneInTheQuiver.class)) {
			victim.getPlayer().damage(20, attacker.getPlayer());
			event.getEntity().remove();
		}
	}

}
