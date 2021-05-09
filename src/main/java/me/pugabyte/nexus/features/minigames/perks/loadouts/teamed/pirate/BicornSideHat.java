package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate;

import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.inventory.ItemStack;

public class BicornSideHat extends BasePirateHat {
	@Override
	public String getName() {
		return "Bicorn Side";
	}

	@Override
	protected ItemStack getColorItem(ColorType color) {
		return getPirateHat(3, color);
	}
}
