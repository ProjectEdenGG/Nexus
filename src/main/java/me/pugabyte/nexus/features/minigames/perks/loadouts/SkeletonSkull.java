package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class SkeletonSkull extends LoadoutPerk {
	@Override
	public String getName() {
		return "Skeleton Skull";
	}

	@Override
	public @NotNull String getDescription() {
		return "Snipe your foes with the incredible prowess of a skeleton";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public Material getMaterial() {
		return Material.SKELETON_SKULL;
	}
}
