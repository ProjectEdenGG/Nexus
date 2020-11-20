package me.pugabyte.nexus.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("HoleInTheWallArena")
public class HoleInTheWallArena extends Arena {
	private List<Location> designHangerLocation = new ArrayList<>();

	public HoleInTheWallArena(Map<String, Object> map) {
		super(map);
		this.designHangerLocation = (List<Location>) map.getOrDefault("designHangerLocation", designHangerLocation);
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("designHangerLocation", designHangerLocation);

		return map;
	}

}
