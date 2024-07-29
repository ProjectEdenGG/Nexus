package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatchBroadcastEvent extends MatchEvent implements Cancellable {
	@Getter
	@Setter
	@NonNull
	private ComponentLike message;
	@Getter
	@Setter
	private Team team;

	public MatchBroadcastEvent(Match match, @NotNull String message) {
		super(match);
		this.message = new JsonBuilder(message);
	}

	public MatchBroadcastEvent(Match match, @NotNull String message, Team team) {
		super(match);
		this.message = new JsonBuilder(message);
		this.team = team;
	}

	public MatchBroadcastEvent(Match match, @NotNull ComponentLike message) {
		super(match);
		this.message = message;
	}

	public MatchBroadcastEvent(Match match, @NotNull ComponentLike message, Team team) {
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

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
