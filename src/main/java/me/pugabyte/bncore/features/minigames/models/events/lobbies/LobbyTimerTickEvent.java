package me.pugabyte.bncore.features.minigames.models.events.lobbies;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.models.Lobby;
import me.pugabyte.bncore.features.minigames.models.Match;

public class LobbyTimerTickEvent extends LobbyEvent {
	@Getter
	private int time;

	public LobbyTimerTickEvent(final Lobby lobby, final Match match, int time) {
		super(lobby, match);
		this.time = time;
	}
}
