package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Archery;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;

import java.util.HashMap;
import java.util.Map;

@Data
@MatchDataFor(Archery.class)
public class ArcheryMatchData extends MatchData {
	private Map<Minigamer, Integer> targetsHit = new HashMap<>();

	public void addToTargets(Minigamer minigamer) {
		int targets = targetsHit.get(minigamer) + 1;
		targetsHit.put(minigamer, targets);
	}

	public int getTargetsHit(Minigamer minigamer) {
		Integer targets = targetsHit.get(minigamer);
		if (targets == null)
			targets = 0;
		return targets;
	}

	public ArcheryMatchData(Match match) {
		super(match);
	}
}
