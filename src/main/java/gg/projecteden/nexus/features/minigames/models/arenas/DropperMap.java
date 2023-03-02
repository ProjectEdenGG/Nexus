package gg.projecteden.nexus.features.minigames.models.arenas;

import gg.projecteden.nexus.features.minigames.models.IHasSpawnpoints;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@SerializableAs("DropperMap")
public class DropperMap implements ConfigurationSerializable, IHasSpawnpoints {
	private String name;
	private List<Location> spawnpoints = new ArrayList<>();
	private Location spectateLocation;

	public DropperMap(Map<String, Object> map) {
		this.name = (String) map.getOrDefault("name", name);
		this.spawnpoints = (List<Location>) map.getOrDefault("spawnpoints", spawnpoints);
		this.spectateLocation = (Location) map.getOrDefault("spectateLocation", spectateLocation);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("name", getName());
			put("spawnpoints", getSpawnpoints());
			put("spectateLocation", getSpectateLocation());
		}};
	}

}
