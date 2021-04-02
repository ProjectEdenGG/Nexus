package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class ZombieSkull extends LoadoutPerk {
	@Override
	public String getName() {
		return "Zombie Skull";
	}

	@Override
	public Material getMaterial() {
		return Material.ZOMBIE_HEAD;
	}

	@Override
	public String getDescription() {
		return "Eek! A zombie! Oh, no, it's just %s";
	}

	@Override
	public int getPrice() {
		return 10;
	}
}
