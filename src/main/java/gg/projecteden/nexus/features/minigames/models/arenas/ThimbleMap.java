package gg.projecteden.nexus.features.minigames.models.arenas;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
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
		return new LinkedHashMap<>() {{
			put("name", getName());
			put("nextTurnLocation", getNextTurnLocation());
			put("spectateLocation", getSpectateLocation());
		}};
	}

}
