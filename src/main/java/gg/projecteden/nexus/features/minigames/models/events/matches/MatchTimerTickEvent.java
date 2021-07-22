package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.HandlerList;

public class MatchTimerTickEvent extends MatchEvent {
	@Getter
	@NonNull
	private int time;

	public MatchTimerTickEvent(final Match match, int time) {
		super(match);
		this.time = time;
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
