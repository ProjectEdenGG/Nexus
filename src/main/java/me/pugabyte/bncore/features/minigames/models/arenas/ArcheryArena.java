package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("ArcheryArena")
public class ArcheryArena extends Arena {

	public ArcheryArena(Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getRegionBaseName() {
		return "archery";
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		return map;
	}
}
