package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Murder;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;

@Data
@MatchDataFor(Murder.class)
public class MurderMatchData extends MatchData {

	Minigamer murderer;
	Minigamer hero;

	public MurderMatchData(Match match) {
		super(match);
	}
}
