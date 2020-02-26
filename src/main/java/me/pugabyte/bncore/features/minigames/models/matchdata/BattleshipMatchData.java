package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;

@Data
@MatchDataFor(Battleship.class)
public class BattleshipMatchData extends MatchData {

	public BattleshipMatchData(Match match) {
		super(match);
	}

}
