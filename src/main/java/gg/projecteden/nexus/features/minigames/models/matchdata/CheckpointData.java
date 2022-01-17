package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.CheckpointArena;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class CheckpointData extends MatchData {
	private Map<UUID, Integer> checkpoints = new HashMap<>();
	private Set<UUID> autoresetting = new HashSet<>();

	public CheckpointData(Match match) {
		super(match);
	}

	public Integer getCheckpointId(Minigamer minigamer) {
		return checkpoints.get(minigamer.getPlayer().getUniqueId());
	}

	public void setCheckpoint(Minigamer minigamer, int id) {
		Integer currentId = getCheckpointId(minigamer);
		if (currentId == null || currentId < id) {
			minigamer.tell("Checkpoint saved (&e#" + id + "&3)");
			checkpoints.put(minigamer.getPlayer().getUniqueId(), id);
		}
	}

	public void toCheckpoint(Minigamer minigamer) {
		CheckpointArena arena = minigamer.getMatch().getArena();

		if (autoresetting.contains(minigamer.getPlayer().getUniqueId())) {
			minigamer.teleportAsync(minigamer.getTeam().getSpawnpoints().get(0));
			minigamer.setScore(0);
		} else if (checkpoints.containsKey(minigamer.getPlayer().getUniqueId()))
			minigamer.teleportAsync(arena.getCheckpoint(getCheckpointId(minigamer)));
		else
			minigamer.teleportAsync(minigamer.getTeam().getSpawnpoints().get(0));
	}

	public void clearData(Minigamer minigamer) {
		checkpoints.remove(minigamer.getPlayer().getUniqueId());
		autoresetting.remove(minigamer.getPlayer().getUniqueId());
	}

	public void autoreset(Minigamer minigamer, Boolean autoreset) {
		UUID uuid = minigamer.getPlayer().getUniqueId();

		if (autoreset == null)
			autoreset = !autoresetting.contains(uuid);

		if (autoreset)
			autoresetting.add(uuid);
		else
			autoresetting.remove(uuid);
	}

	public boolean isAutoresetting(Minigamer minigamer) {
		return autoresetting.contains(minigamer.getPlayer().getUniqueId());
	}
}
