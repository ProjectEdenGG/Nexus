package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class OakLeavesHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Oak Leaves";
	}

	@Override
	public String getDescription() {
		return "Wear the leaves of a real tree";
	}

	@Override
	public int getPrice() {
		return 5;
	}

	@Override
	public Material getMaterial() {
		return Material.OAK_LEAVES;
	}
}
