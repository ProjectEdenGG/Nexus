package me.pugabyte.bncore.features.minigames.models;

import lombok.NoArgsConstructor;
import lombok.ToString;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;

@NoArgsConstructor
public class MatchData {
	@ToString.Exclude
	private Match match;
	protected static WorldGuardUtils WGUtils = Minigames.getWorldGuardUtils();
	protected static WorldEditUtils WEUtils = Minigames.getWorldEditUtils();

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
