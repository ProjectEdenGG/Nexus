package me.pugabyte.nexus.features.minigames.models.events.matches.teams;

import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEvent;

public abstract class TeamEvent extends MatchEvent {
	@Getter
	private final Team team;

	public TeamEvent(Match match, Team team) {
		super(match);
		this.team = team;
	}

}
