package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class DiamondOreHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Diamond Ore";
	}

	@Override
	public String getDescription() {
		return "The most precious element of our overworld";
	}

	@Override
	public int getPrice() {
		return 25;
	}

	@Override
	public Material getMaterial() {
		return Material.DIAMOND_ORE;
	}
}
