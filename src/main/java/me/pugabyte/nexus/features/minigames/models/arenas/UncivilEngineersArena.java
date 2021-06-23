package me.pugabyte.nexus.features.minigames.models.arenas;

import lombok.Data;
import lombok.ToString;
import me.pugabyte.nexus.features.minigames.mechanics.UncivilEngineers;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
@SerializableAs("UncivilEngineersArena")
public class UncivilEngineersArena extends CheckpointArena {

	public LinkedHashMap<Integer, Location> origins;
	public List<UncivilEngineers.MobPoint> mobPoints = new ArrayList<>();

	public UncivilEngineersArena(Map<String, Object> map) {
		super(map);
		origins = (LinkedHashMap<Integer, Location>) map.getOrDefault("origins", new LinkedHashMap<Integer, Location>());
		mobPoints = (List<UncivilEngineers.MobPoint>) map.getOrDefault("mobPoints", mobPoints);
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("origins", origins);
		map.put("mobPoints", mobPoints);
		return map;
	}


}
