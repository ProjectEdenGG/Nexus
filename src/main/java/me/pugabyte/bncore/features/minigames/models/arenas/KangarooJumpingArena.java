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
@SerializableAs("KangarooJumpingArena")
public class KangarooJumpingArena extends Arena {

	private List<Location> powerUpLocations = new ArrayList<>();

	public KangarooJumpingArena(Map<String, Object> map) {
		super(map);
		this.powerUpLocations = (List<Location>) map.getOrDefault("powerUpLocations", powerUpLocations);
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("powerUpLocations", powerUpLocations);

		return map;
	}

}
