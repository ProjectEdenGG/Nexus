package me.pugabyte.nexus.features.minigames.models.events.matches;

import me.pugabyte.nexus.features.minigames.models.Match;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class MatchInitializeEvent extends MatchEvent implements Cancellable {

	public MatchInitializeEvent(final Match match) {
		super(match);
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
