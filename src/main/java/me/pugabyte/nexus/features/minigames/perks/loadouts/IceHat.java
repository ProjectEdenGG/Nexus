package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class IceHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Ice Cube";
	}

	@Override
	public @NotNull String getDescription() {
		return "Keep yourself cool in the summer with this cube of ice";
	}

	@Override
	public int getPrice() {
		return 15;
	}

	@Override
	public Material getMaterial() {
		return Material.ICE;
	}
}
