package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("ThimbleMap")
public class ThimbleMap implements ConfigurationSerializable {
	private String name;

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("name", getName());
		}};
	}

	public ThimbleMap(Map<String, Object> map) {
		this.name = (String) map.get("name");
	}

}
