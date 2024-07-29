package gg.projecteden.nexus.features.damagetracker.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class DamageEvent extends Event {
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	@Setter
	@Getter
	private LivingEntity entity;
	@Setter
	@Getter
	private Object damager;
	@Setter
	@Getter
	private double damage;
	@Setter
	@Getter
	private DamageCause damageCause;
	@Setter
	@Getter
	private LocalDateTime time;
	@Getter
	private EntityDamageEvent event;

	public DamageEvent(LivingEntity entity, Object damager, DamageCause damageCause, double damage, EntityDamageEvent event) {
		this.entity = entity;
		this.damager = damager;
		this.damage = damage;
		this.damageCause = damageCause;
		this.time = LocalDateTime.now();
		this.event = event;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
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
