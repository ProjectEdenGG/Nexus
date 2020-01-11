package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@SerializableAs("ThimbleMap")
public class ThimbleMap implements ConfigurationSerializable {
	private String name;
	private Location nextTurnLocation;
	private Location spectateLocation;

	public ThimbleMap(Map<String, Object> map) {
		this.name = (String) map.get("name");
		this.nextTurnLocation = (Location) map.get("nextTurnLocation");
		this.spectateLocation = (Location) map.get("spectateLocation");
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("name", getName());
			put("nextTurnLocation", getNextTurnLocation());
			put("spectateLocation", getSpectateLocation());
		}};
	}

}
