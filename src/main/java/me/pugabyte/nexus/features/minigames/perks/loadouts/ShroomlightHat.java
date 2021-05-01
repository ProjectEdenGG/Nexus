package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ShroomlightHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Shroomlight";
	}

	@Override
	public @NotNull String getDescription() {
		return "Illuminate like the lights of something out of this world";
	}

	@Override
	public int getPrice() {
		return 20;
	}

	@Override
	public Material getMaterial() {
		return Material.SHROOMLIGHT;
	}
}
