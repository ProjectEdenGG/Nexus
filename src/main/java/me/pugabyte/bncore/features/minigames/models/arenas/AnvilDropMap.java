package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@SerializableAs("AnvilDropMap")
public class AnvilDropMap implements ConfigurationSerializable {
	private String name;
	private Location spectateLocation;
	private List<Location> spawnpoints;

	public AnvilDropMap(Map<String, Object> map) {
		this.name = (String) map.get("name");
		this.spectateLocation = (Location) map.get("spectateLocation");
		this.spawnpoints = (List<Location>) map.get("spawnpoints");
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("name", getName());
			put("spectateLocation", getSpectateLocation());
			put("spawnpoints", getSpawnpoints());
		}};
	}
}
