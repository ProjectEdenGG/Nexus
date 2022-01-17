package gg.projecteden.nexus.features.minigames.models.events.matches.teams;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEvent;
import lombok.Getter;

public abstract class TeamEvent extends MatchEvent {
	@Getter
	private final Team team;

	public TeamEvent(Match match, Team team) {
		super(match);
		this.team = team;
	}

}
