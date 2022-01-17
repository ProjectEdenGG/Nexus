package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Team;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class MatchBroadcastEvent extends MatchEvent implements Cancellable {
	@Getter
	@Setter
	@NonNull
	private String message;
	@Getter
	@Setter
	private Team team;

	public MatchBroadcastEvent(Match match, String message) {
		super(match);
		this.message = message;
	}

	public MatchBroadcastEvent(Match match, String message, Team team) {
		super(match);
		this.message = message;
		this.team = team;
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
