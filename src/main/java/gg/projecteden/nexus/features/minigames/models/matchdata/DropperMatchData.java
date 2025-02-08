package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Dropper;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperMap;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@MatchDataFor(Dropper.class)
public class DropperMatchData extends MatchData {
	private final Set<Minigamer> finished = new HashSet<>();
	private final Set<DropperMap> playedMaps = new HashSet<>();

	public DropperMatchData(Match match) {
		super(match);
	}

	public int getRound() {
		return playedMaps.size() + 1;
	}


}
