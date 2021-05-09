package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class JungleLeavesHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Jungle Leaves";
	}

	@Override
	public @NotNull String getDescription() {
		return "Wear the leaves of a real tree";
	}

	@Override
	public int getPrice() {
		return 5;
	}

	@Override
	public Material getMaterial() {
		return Material.JUNGLE_LEAVES;
	}
}
