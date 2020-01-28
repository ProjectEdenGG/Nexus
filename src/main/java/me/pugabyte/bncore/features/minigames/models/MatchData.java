package me.pugabyte.bncore.features.minigames.models;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
public class MatchData {
	@ToString.Exclude
	private Match match;

	public MatchData(Match match) {
		this.match = match;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

}
