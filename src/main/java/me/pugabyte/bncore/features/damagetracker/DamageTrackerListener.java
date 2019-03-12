package me.pugabyte.bncore.features.damagetracker;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.damagetracker.models.DamageEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageTrackerListener implements Listener {

	public DamageTrackerListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onDamage(DamageEvent event) {
		DamageTracker.log(event);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Entity damager = null;
		DamageEvent damageEvent = new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event);
		BNCore.getInstance().getServer().getPluginManager().callEvent(damageEvent);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Entity damager;
		if (event.getDamager() instanceof Projectile) {
			damager = (Entity) ((Projectile) event.getDamager()).getShooter();
		} else {
			damager = event.getDamager();
		}

		DamageEvent damageEvent = new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event);
		BNCore.getInstance().getServer().getPluginManager().callEvent(damageEvent);
	}

	@EventHandler
	public void onDamage(EntityDamageByBlockEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Block damager = event.getDamager();

		DamageEvent damageEvent = new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event);
		BNCore.getInstance().getServer().getPluginManager().callEvent(damageEvent);
	}

	boolean ignore(EntityDamageEvent event) {
		if (event.isCancelled())
			return true;
		return !(event.getEntity() instanceof LivingEntity);
	}

}
