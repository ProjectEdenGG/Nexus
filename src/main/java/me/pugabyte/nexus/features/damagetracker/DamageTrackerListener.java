package me.pugabyte.nexus.features.damagetracker;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.damagetracker.models.DamageEvent;
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
		Nexus.registerListener(this);
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
		new DamageEvent(entity, null, event.getCause(), event.getDamage(), event).callEvent();
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Entity damager;
		if (event.getDamager() instanceof Projectile projectile) {
			damager = (Entity) projectile.getShooter();
		} else {
			damager = event.getDamager();
		}

		new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event).callEvent();
	}

	@EventHandler
	public void onDamage(EntityDamageByBlockEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Block damager = event.getDamager();

		new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event).callEvent();
	}

	boolean ignore(EntityDamageEvent event) {
		if (event.isCancelled())
			return true;
		return !(event.getEntity() instanceof LivingEntity);
	}

}
