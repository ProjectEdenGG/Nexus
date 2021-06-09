package me.pugabyte.nexus.features.minigames.models.events.matches.lobbies;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Lobby;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEvent;

public abstract class LobbyEvent extends MatchEvent {
	@NonNull
	@Getter
	private Lobby lobby;

	public LobbyEvent(Match match, Lobby lobby) {
		super(match);
		this.lobby = lobby;
	}

}
