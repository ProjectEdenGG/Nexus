package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatchRegeneratedEvent extends MatchEvent implements Cancellable {

	public MatchRegeneratedEvent(final Match match) {
		super(match);
	}

	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
