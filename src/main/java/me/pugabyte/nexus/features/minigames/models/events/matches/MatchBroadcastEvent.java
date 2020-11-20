package me.pugabyte.nexus.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Team;

public class MatchBroadcastEvent extends MatchEvent {
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

}
