package me.pugabyte.bncore.features.minigames.models.events.matches.lobbies;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Lobby;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEvent;

public class LobbyEvent extends MatchEvent {
	@NonNull
	@Getter
	private Lobby lobby;

	public LobbyEvent(Match match, Lobby lobby) {
		super(match);
		this.lobby = lobby;
	}

}
