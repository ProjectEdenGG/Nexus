package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;

@Data
@MatchDataFor(TicTacToe.class)
public class TicTacToeMatchData extends MatchData {

	public TicTacToeMatchData(Match match) {
		super(match);
	}

	public long end() {
		// tasks
		return 0; // cumulative tick delay of tasks run in this method
	}

}
