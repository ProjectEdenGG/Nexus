package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class GrassBlockHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Grass Block";
	}

	@Override
	public String getDescription() {
		return "The most basic element of life on our planet";
	}

	@Override
	public int getPrice() {
		return 1;
	}

	@Override
	public Material getMaterial() {
		return Material.GRASS_BLOCK;
	}
}
