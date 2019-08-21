package me.pugabyte.bncore.features.minigames.models.events.lobbies;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.Lobby;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.events.MinigameEvent;

public class LobbyEvent extends MinigameEvent {
	@NonNull
	@Getter
	private Lobby lobby;
	@NonNull
	@Getter
	private Match match;

	public LobbyEvent(Lobby lobby, Match match) {
		super(match.getArena());
		this.lobby = lobby;
		this.match = match;
	}

}
