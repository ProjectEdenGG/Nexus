package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class MatchRegeneratedEvent extends MatchEvent implements Cancellable {

	public MatchRegeneratedEvent(final Match match) {
		super(match);
	}

	@Getter
	@Setter
	protected boolean cancelled = false;

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
