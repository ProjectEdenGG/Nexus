package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class UnicornHorn extends LoadoutPerk {
	@Override
	public Material getMaterial() {
		return Material.END_ROD;
	}

	@Override
	public String getName() {
		return "Unicorn Horn";
	}

	@Override
	public String getDescription() {
		return "Become a pretty unicorn with this glowing horn on top of your head!";
	}

	@Override
	public int getPrice() {
		return 25;
	}
}
