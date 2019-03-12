package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Team;

import java.util.HashMap;
import java.util.Map;

public class CaptureTheFlagMatchData extends MatchData {
	@Getter
	@Setter
	Map<Team, Flag> flags = new HashMap<>();

	public CaptureTheFlagMatchData(Match match) {
		super(match);
	}

	public Flag getFlag(Team team) {
		if (flags.containsKey(team)) {
			return flags.get(team);
		}
		return null;
	}

	public void addFlag(Team team, Flag flag) {
		flags.put(team, flag);
	}

}
