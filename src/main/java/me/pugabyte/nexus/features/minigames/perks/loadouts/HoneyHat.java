package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class HoneyHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Honey Block";
	}

	@Override
	public String getDescription() {
		return "Encase yourself in a delicious block of honey";
	}

	@Override
	public int getPrice() {
		return 15;
	}

	@Override
	public Material getMaterial() {
		return Material.HONEY_BLOCK;
	}
}
