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
@SerializableAs("MurderArena")
public class MurderArena extends Arena {
	private List<Location> scrapPoints = new ArrayList<>();
	private int spawnChance;

	public MurderArena(Map<String, Object> map) {
		super(map);
		scrapPoints = (List<Location>) map.getOrDefault("scrapPoints", new ArrayList<Location>());
		spawnChance = (int) map.getOrDefault("spawnChance", 0);
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("scrapPoints", scrapPoints);
		map.put("spawnChance", spawnChance);
		return map;
	}


}
