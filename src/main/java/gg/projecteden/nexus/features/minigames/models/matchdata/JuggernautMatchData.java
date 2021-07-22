package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Juggernaut;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;

@Data
@MatchDataFor(Juggernaut.class)
public class JuggernautMatchData extends MatchData {
	private Minigamer lastAttacker = null;

	public JuggernautMatchData(Match match) {
		super(match);
	}
}
