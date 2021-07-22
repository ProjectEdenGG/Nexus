package gg.projecteden.nexus.features.minigames.models.events.matches.lobbies;

import gg.projecteden.nexus.features.minigames.models.Lobby;
import gg.projecteden.nexus.features.minigames.models.Match;
import lombok.Getter;
import org.bukkit.event.HandlerList;

public class LobbyTimerTickEvent extends LobbyEvent {
	@Getter
	private int time;

	public LobbyTimerTickEvent(final Match match, final Lobby lobby, int time) {
		super(match, lobby);
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
