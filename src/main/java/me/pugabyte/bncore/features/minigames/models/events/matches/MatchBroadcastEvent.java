package me.pugabyte.bncore.features.minigames.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Team;

public class MatchBroadcastEvent extends MatchEvent {
	@NonNull
	@Getter
	@Setter
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
