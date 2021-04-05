package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;

public class WoolHat extends TeamLoadoutPerk {
	@Override
	public String getName() {
		return "Wool Hood";
	}

	@Override
	public String getDescription() {
		return "Keep yourself extra warm with this wool covering your head";
	}

	@Override
	public int getPrice() {
		return 25;
	}

	@Override
	protected Material getColorMaterial(ColorType color) {
		return color.getWool();
	}
}
