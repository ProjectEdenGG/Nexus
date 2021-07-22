package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.UncivilEngineers;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.features.minigames.mechanics.UncivilEngineers.getStart;
import static gg.projecteden.nexus.features.minigames.mechanics.UncivilEngineers.offset;

@Getter
@MatchDataFor(UncivilEngineers.class)
public class UncivilEngineersMatchData extends CheckpointData {
	private final Map<UUID, Integer> slices = new HashMap<>();

	public UncivilEngineersMatchData(Match match) {
		super(match);
	}

	public void assignSlice(Minigamer minigamer, int id) {
		slices.put(minigamer.getUniqueId(), id);
		minigamer.teleport(offset(getStart(), id));
	}

	public int getSlice(Minigamer minigamer) {
		final UUID uuid = minigamer.getUniqueId();
		if (!slices.containsKey(uuid))
			throw new InvalidInputException("[UncivilEngineers] Could not find slice number for " + minigamer.getNickname());

		return slices.get(uuid);
	}

}
