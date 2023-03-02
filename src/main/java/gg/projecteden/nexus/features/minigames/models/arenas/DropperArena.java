package gg.projecteden.nexus.features.minigames.models.arenas;

import gg.projecteden.nexus.features.minigames.models.Arena;
import lombok.Data;
import lombok.ToString;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
@SerializableAs("DropperArena")
public class DropperArena extends Arena {
	private List<DropperMap> maps;

	private DropperMap currentMap;

	public DropperArena(Map<String, Object> map) {
		super(map);
		this.maps = (List<DropperMap>) map.get("maps");

		if (maps != null)
			currentMap = maps.get(0);
	}

	@Override
	public @NotNull String getRegionBaseName() {
		return "dropper_" + currentMap.getName().toLowerCase();
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("maps", getMaps());
		return map;
	}

}
