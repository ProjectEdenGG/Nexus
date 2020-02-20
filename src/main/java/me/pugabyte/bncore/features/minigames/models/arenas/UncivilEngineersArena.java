package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.UncivilEngineers;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("UncivilEngineersArena")
public class UncivilEngineersArena extends Arena {

	public LinkedHashMap<Integer, Location> origins;
	public List<UncivilEngineers.MobPoint> mobPoints;

	public UncivilEngineersArena(Map<String, Object> map) {
		super(map);
		origins = (LinkedHashMap<Integer, Location>) map.getOrDefault("origins", new LinkedHashMap<Integer, Location>());
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("origin", origins);
		return map;
	}


}
