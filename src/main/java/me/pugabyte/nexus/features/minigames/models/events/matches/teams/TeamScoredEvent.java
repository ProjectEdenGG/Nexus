package me.pugabyte.nexus.features.minigames.models.events.matches.teams;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Team;

public class TeamScoredEvent extends TeamEvent {
	@Getter
	@Setter
	private int amount;

	public TeamScoredEvent(Match match, Team team, int amount) {
		super(match, team);
		this.amount = amount;
	}

}
