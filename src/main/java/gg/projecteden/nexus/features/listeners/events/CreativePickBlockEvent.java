package gg.projecteden.nexus.features.listeners.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CreativePickBlockEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	public CreativePickBlockEvent(@NotNull Player who) {
		super(who);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
