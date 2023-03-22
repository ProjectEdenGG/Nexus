package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.HandlerList;

public class MatchJoinEvent extends MatchEvent {
	@Getter
	@NonNull
	private Minigamer minigamer;

	public MatchJoinEvent(Match match, Minigamer minigamer) {
		super(match);
		this.minigamer = minigamer;
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
