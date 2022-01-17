package gg.projecteden.nexus.features.minigames.models.events.matches.teams;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TeamScoredEvent extends TeamEvent implements Cancellable {
	@Getter
	@Setter
	private int amount;

	public TeamScoredEvent(Match match, Team team, int amount) {
		super(match, team);
		this.amount = amount;
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
