package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.Juggernaut;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;

@Data
@MatchDataFor(Juggernaut.class)
public class JuggernautMatchData extends MatchData {
	private Minigamer lastAttacker = null;

	public JuggernautMatchData(Match match) {
		super(match);
	}
}
