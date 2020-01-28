package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("DeathSwapArena")
public class DeathSwapArena extends Arena {
	private List<Location> schematicResetLocations;

	public DeathSwapArena(Map<String, Object> map) {
		super(map);
		this.schematicResetLocations = (List<Location>) map.get("schematicResetLocations");
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("schematicResetLocations", getSchematicResetLocations());

		return map;
	}

}
