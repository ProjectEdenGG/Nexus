package gg.projecteden.nexus.features.damagetracker.models;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.time.LocalDateTime;
import java.util.List;

public class DamageEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private LivingEntity entity;
	private Object damager;
	private double damage;
	private DamageCause damageCause;
	private LocalDateTime time;
	private EntityDamageEvent event;

	public DamageEvent(LivingEntity entity, Object damager, DamageCause damageCause, double damage, EntityDamageEvent event) {
		this.entity = entity;
		this.damager = damager;
		this.damage = damage;
		this.damageCause = damageCause;
		this.time = LocalDateTime.now();
		this.event = event;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	public Object getDamager() {
		return damager;
	}

	public void setDamager(Object damager) {
		this.damager = damager;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public DamageCause getDamageCause() {
		return damageCause;
	}

	public void setDamageCause(DamageCause damageCause) {
		this.damageCause = damageCause;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public EntityDamageEvent getEvent() {
		return event;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean existsIn(List<DamageEvent> events) {
		for (DamageEvent event : events) {
			if (!this.getTime().equals(event.getTime())) {
				continue;
			}
			if (this.getDamager() != null && !this.getDamager().equals(event.getDamager())) {
				continue;
			}
			if (!this.getDamageCause().equals(event.getDamageCause())) {
				continue;
			}
			if (this.getDamage() != event.getDamage()) {
				continue;
			}
			return true;
		}
		return false;
	}
}
