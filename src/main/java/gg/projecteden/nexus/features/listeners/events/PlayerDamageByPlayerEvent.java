package gg.projecteden.nexus.features.listeners.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageByPlayerEvent extends PlayerEvent {
	@NonNull
	@Getter
	final Player attacker;
	@NonNull
	@Getter
	final EntityDamageByEntityEvent originalEvent;

	@SneakyThrows
	public PlayerDamageByPlayerEvent(@NotNull Player victim, @NotNull Player attacker, @NotNull EntityDamageByEntityEvent event) {
		super(victim);
		this.attacker = attacker;
		this.originalEvent = event;
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
