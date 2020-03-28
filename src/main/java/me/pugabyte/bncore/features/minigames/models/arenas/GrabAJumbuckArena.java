package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.SerializationUtils;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@SerializableAs("GrabAJumbuckArena")
public class GrabAJumbuckArena extends Arena {
	private Set<Material> sheepSpawnBlocks = new HashSet<>();

	public GrabAJumbuckArena(Map<String, Object> map) {
		super(map);
		this.sheepSpawnBlocks = SerializationUtils.yml_deserializeMaterialSet((List<String>) map.getOrDefault("sheepSpawnBlocks", new ArrayList<>()));
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("sheepSpawnBlocks", SerializationUtils.yml_serializeMaterialSet(sheepSpawnBlocks));

		return map;
	}


}
