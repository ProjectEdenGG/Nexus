package me.pugabyte.nexus.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class MatchQuitEvent extends MatchEvent implements Cancellable {
	@Getter
	@NonNull
	private Minigamer minigamer;

	public MatchQuitEvent(Match match, Minigamer minigamer) {
		super(match);
		this.minigamer = minigamer;
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
