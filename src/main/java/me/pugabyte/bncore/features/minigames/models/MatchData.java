package me.pugabyte.bncore.features.minigames.models;

import lombok.NoArgsConstructor;
import lombok.ToString;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;

@NoArgsConstructor
public class MatchData {
	@ToString.Exclude
	private Match match;
	protected WorldGuardUtils WGUtils;
	protected WorldEditUtils WEUtils;

	public MatchData(Match match) {
		this.match = match;
		WGUtils = match.getArena().getWGUtils();
		WEUtils = match.getArena().getWEUtils();
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

}
