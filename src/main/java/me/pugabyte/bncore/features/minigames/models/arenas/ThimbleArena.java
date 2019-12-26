package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("ThimbleArena")
public class ThimbleArena extends Arena {
	private List<ThimbleMap> thimbleMaps;

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("thimbleMaps", getThimbleMaps());

		return map;
	}

	public ThimbleArena(Map<String, Object> map) {
		super(map);
		setThimbleMaps((List<ThimbleMap>) map.get("thimbleMaps"));
	}

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

}