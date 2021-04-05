package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;

public class ColorfulGlassHat extends TeamLoadoutPerk {
	@Override
	public String getName() {
		return "Stained Glass";
	}

	@Override
	public String getDescription() {
		return "Become a colorful astronaut with this stained glass hat";
	}

	@Override
	public int getPrice() {
		return 35;
	}

	@Override
	protected Material getColorMaterial(ColorType color) {
		return color.getStainedGlass();
	}
}
