package me.pugabyte.bncore.features.minigames.models.events;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class MinigameEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	@NonNull
	private Arena arena;
	private boolean cancelled = false;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
