package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Murder;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;

@Data
@MatchDataFor(Murder.class)
public class MurderMatchData extends MatchData {

	public MurderMatchData(Match match) {
		super(match);
	}
}
