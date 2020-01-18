package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("GoldRushArena")
public class GoldRushArena extends Arena {

	private int mineStackHeight;

	public GoldRushArena(Map<String, Object> map) {
		super(map);
		this.mineStackHeight = (int) map.get("mineStackHeight");
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("mineStackHeight", mineStackHeight);

		return map;
	}


}
