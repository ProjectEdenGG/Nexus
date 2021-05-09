package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class GlassHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Glass";
	}

	@Override
	public @NotNull String getDescription() {
		return "Become an astronaut with this shiny glass hat";
	}

	@Override
	public int getPrice() {
		return 15;
	}

	@Override
	public Material getMaterial() {
		return Material.GLASS;
	}
}
