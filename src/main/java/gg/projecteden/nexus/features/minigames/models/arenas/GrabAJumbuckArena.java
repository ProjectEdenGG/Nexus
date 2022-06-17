package gg.projecteden.nexus.features.minigames.models.arenas;

import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.SerializationUtils.YML;
import lombok.Data;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@ToString(callSuper = true)
@SerializableAs("GrabAJumbuckArena")
public class GrabAJumbuckArena extends Arena {
	private Set<Material> sheepSpawnBlocks = new HashSet<>();

	public GrabAJumbuckArena(Map<String, Object> map) {
		super(map);
		this.sheepSpawnBlocks = YML.deserializeMaterialSet((List<String>) map.getOrDefault("sheepSpawnBlocks", new ArrayList<>()));
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("sheepSpawnBlocks", YML.serializeMaterialSet(sheepSpawnBlocks));

		return map;
	}

}
