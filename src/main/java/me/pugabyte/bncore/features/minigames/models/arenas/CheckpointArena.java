package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("CheckpointArena")
public class CheckpointArena extends Arena {
	private List<Location> checkpoints = new ArrayList<>();

	public CheckpointArena(Map<String, Object> map) {
		super(map);
		this.checkpoints = (List<Location>) map.getOrDefault("checkpoints", checkpoints);
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("checkpoints", getCheckpoints());

		return map;
	}

	public Location getCheckpoint(int id) {
		return checkpoints.get(id - 1);
	}

	public void setCheckpoint(int id, Location location) {
		if (checkpoints.size() <= (id - 1))
			checkpoints.add(location);
		else
			checkpoints.set(id - 1, location);
	}

	public void removeCheckpoint(int id) {
		if (checkpoints.size() > (id - 1))
			checkpoints.remove(id - 1);
	}

}
