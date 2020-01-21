package me.pugabyte.bncore.features.minigames.models.arenas;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Thimble;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("ThimbleArena")
public class ThimbleArena extends Arena {
	private List<ThimbleMap> thimbleMaps;

	private ThimbleMap currentMap;
	private Thimble.ThimbleGamemode gamemode;

	public ThimbleArena(Map<String, Object> map) {
		super(map);
		this.thimbleMaps = (List<ThimbleMap>) map.get("thimbleMaps");

		currentMap = thimbleMaps.get(0);
	}

	@Override
	public String getRegionBaseName() {
		return "thimble_" + currentMap.getName().toLowerCase();
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("thimbleMaps", getThimbleMaps());

		return map;
	}

	public Thimble.ThimbleGamemode getNextGamemode() {
		if (gamemode != null)
			if (Thimble.ClassicGamemode.class.equals(gamemode.getClass()))
				return new Thimble.RiskGamemode();
			else if (Thimble.RiskGamemode.class.equals(gamemode.getClass()))
				return new Thimble.LastManStandingGamemode();
			else if (Thimble.LastManStandingGamemode.class.equals(gamemode.getClass()))
				return new Thimble.ClassicGamemode();

		return Arrays.asList(new Thimble.ClassicGamemode(), new Thimble.RiskGamemode(), new Thimble.LastManStandingGamemode()).get(Utils.randomInt(0, 2));
	}


}