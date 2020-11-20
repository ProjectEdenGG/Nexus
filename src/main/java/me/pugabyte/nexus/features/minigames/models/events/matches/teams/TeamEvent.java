package me.pugabyte.nexus.features.minigames.models.events.matches.teams;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEvent;

public class TeamEvent extends MatchEvent {
	@Getter
	@Setter
	private Team team;

	public TeamEvent(Match match, Team team) {
		super(match);
		this.team = team;
	}

}
