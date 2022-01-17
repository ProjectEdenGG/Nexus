package gg.projecteden.nexus.features.minigames.models.events.matches.minigamers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MinigamerDamageEvent extends MinigamerEvent implements Cancellable {
	@Getter
	private Minigamer attacker;
	@Getter
	private Event originalEvent;

	public MinigamerDamageEvent(Minigamer victim) {
		super(victim);
	}

	public MinigamerDamageEvent(Minigamer victim, Event originalEvent) {
		super(victim);
		this.originalEvent = originalEvent;
	}

	public MinigamerDamageEvent(Minigamer victim, Minigamer attacker, Event originalEvent) {
		super(victim);
		this.attacker = attacker;
		this.originalEvent = originalEvent;
	}

	protected boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
