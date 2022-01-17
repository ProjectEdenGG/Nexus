package gg.projecteden.nexus.features.minigames.models.events.matches.lobbies;

import gg.projecteden.nexus.features.minigames.models.Lobby;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEvent;
import lombok.Getter;
import lombok.NonNull;

public abstract class LobbyEvent extends MatchEvent {
	@NonNull
	@Getter
	private Lobby lobby;

	public LobbyEvent(Match match, Lobby lobby) {
		super(match);
		this.lobby = lobby;
	}

}
