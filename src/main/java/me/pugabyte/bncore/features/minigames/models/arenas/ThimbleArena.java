package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Thimble;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("ThimbleArena")
public class ThimbleArena extends Arena {
	private List<ThimbleMap> thimbleMaps;

	private ThimbleMap currentMap;
	private Thimble.ThimbleGamemode gamemode;
	private String poolRegionStr;

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("thimbleMaps", getThimbleMaps());

		return map;
	}

	public ThimbleArena(Map<String, Object> map) {
		super(map);
		this.thimbleMaps = (List<ThimbleMap>) map.get("thimbleMaps");

		currentMap = thimbleMaps.get(0);
		poolRegionStr = "thimble_" + thimbleMaps.get(0).getName() + "_pool";
	}

	public Thimble.ThimbleGamemode getNextGamemode() {
		if (gamemode != null)
			if (Thimble.PointsGamemode.class.equals(gamemode.getClass()))
				return new Thimble.RiskGamemode();
			else if (Thimble.RiskGamemode.class.equals(gamemode.getClass()))
				return new Thimble.LastManStandingGamemode();
			else if (Thimble.LastManStandingGamemode.class.equals(gamemode.getClass()))
				return new Thimble.PointsGamemode();

		return new Thimble.PointsGamemode();
	}


}