package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.Murder;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;

@Data
@MatchDataFor(Murder.class)
public class MurderMatchData extends MatchData {

	public MurderMatchData(Match match) {
		super(match);
	}
}
