package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class CreeperSkull extends LoadoutPerk {
	@Override
	public String getName() {
		return "Creeper Head";
	}

	@Override
	public String getDescription() {
		return "A disguise so scary your enemies will be saying \"aww man!\"";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public Material getMaterial() {
		return Material.CREEPER_HEAD;
	}
}
