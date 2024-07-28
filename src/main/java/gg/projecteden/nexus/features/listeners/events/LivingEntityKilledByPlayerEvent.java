package gg.projecteden.nexus.features.listeners.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LivingEntityKilledByPlayerEvent extends EntityEvent {
	@NonNull
	@Getter
	final LivingEntity entity;
	@NonNull
	@Getter
	final Player attacker;
	@NonNull
	@Getter
	final EntityDamageByEntityEvent originalEvent;

	@SneakyThrows
	public LivingEntityKilledByPlayerEvent(@NotNull LivingEntity victim, @NotNull Player attacker, @NotNull EntityDamageByEntityEvent event) {
		super(victim);
		this.entity = victim;
		this.attacker = attacker;
		this.originalEvent = event;
	}

	public void drop(ItemStack item) {
		attacker.getWorld().dropItem(entity.getLocation(), item);
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
