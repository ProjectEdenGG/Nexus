package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.CheckpointArena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class CheckpointData extends MatchData {
	private Map<UUID, Integer> checkpoints = new HashMap<>();
	private List<UUID> autoresetting = new ArrayList<>();

	public CheckpointData(Match match) {
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

		if (autoresetting.contains(minigamer.getPlayer().getUniqueId())) {
			minigamer.teleport(minigamer.getTeam().getSpawnpoints().get(0));
			minigamer.setScore(0);
		} else if (checkpoints.containsKey(minigamer.getPlayer().getUniqueId()))
			minigamer.teleport(arena.getCheckpoint(getCheckpointId(minigamer)));
		else
			minigamer.teleport(minigamer.getTeam().getSpawnpoints().get(0));
	}

	public void clearData(Minigamer minigamer) {
		checkpoints.remove(minigamer.getPlayer().getUniqueId());
		autoresetting.remove(minigamer.getPlayer().getUniqueId());
	}

	public void autoreset(Minigamer minigamer, boolean autoreset) {
		if (autoreset)
			autoresetting.add(minigamer.getPlayer().getUniqueId());
		else
			autoresetting.remove(minigamer.getPlayer().getUniqueId());
	}

	public boolean isAutoresetting(Minigamer minigamer) {
		return autoresetting.contains(minigamer.getPlayer().getUniqueId());
	}
}
