package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class MushroomHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Mushroom Block";
	}

	@Override
	public String getDescription() {
		return "Mushroom Hat||&3Mushroom Hat||&3Whatever could it mean?";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public Material getMaterial() {
		return Material.RED_MUSHROOM_BLOCK;
	}
}
