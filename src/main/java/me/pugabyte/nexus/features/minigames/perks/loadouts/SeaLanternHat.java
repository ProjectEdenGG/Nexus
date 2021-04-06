package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class SeaLanternHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Sea Lantern";
	}

	@Override
	public String getDescription() {
		return "Illuminate like the lights of something under the sea";
	}

	@Override
	public int getPrice() {
		return 20;
	}

	@Override
	public Material getMaterial() {
		return Material.SEA_LANTERN;
	}
}
