package me.pugabyte.bncore.features.minigames.models.events.matches.teams;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Team;

public class TeamScoredEvent extends TeamEvent {
	@Getter
	@Setter
	private int amount;

	public TeamScoredEvent(Match match, Team team, int amount) {
		super(match, team);
		this.amount = amount;
	}

}
