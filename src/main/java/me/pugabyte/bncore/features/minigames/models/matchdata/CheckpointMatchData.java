package me.pugabyte.bncore.features.minigames.models.matchdata;

import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.arenas.CheckpointArena;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MatchDataFor(CheckpointMechanic.class)
public class CheckpointMatchData extends MatchData {
	private Map<UUID, Integer> checkpoints = new HashMap<>();

	public CheckpointMatchData(Match match) {
		super(match);
	}

	public Integer getCheckpointId(Minigamer minigamer) {
		return checkpoints.get(minigamer.getPlayer().getUniqueId());
	}

	public void setCheckpoint(Minigamer minigamer, int id) {
		Integer currentId = getCheckpointId(minigamer);
		if (currentId == null || currentId < id)
			checkpoints.put(minigamer.getPlayer().getUniqueId(), id);
	}

	public void toCheckpoint(Minigamer minigamer) {
		CheckpointArena arena = minigamer.getMatch().getArena();

		if (checkpoints.containsKey(minigamer.getPlayer().getUniqueId()))
			minigamer.teleport(arena.getCheckpoint(getCheckpointId(minigamer)));
		else
			minigamer.teleport(minigamer.getTeam().getSpawnpoints().get(0));
	}

	public void clearCheckpoints(Minigamer minigamer) {
		checkpoints.remove(minigamer.getPlayer().getUniqueId());
	}
}
