package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class NetheriteHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Netherite Block";
	}

	@Override
	public @NotNull String getDescription() {
		return "Encase yourself in the most valuable block known to man";
	}

	@Override
	public int getPrice() {
		return 75;
	}

	@Override
	public Material getMaterial() {
		return Material.NETHERITE_BLOCK;
	}
}
