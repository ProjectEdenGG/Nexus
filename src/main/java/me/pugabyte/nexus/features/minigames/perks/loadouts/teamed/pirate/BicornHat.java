package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate;

import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.inventory.ItemStack;

public class BicornHat extends BasePirateHat {
	@Override
	public String getName() {
		return "Bicorn";
	}

	@Override
	protected ItemStack getColorItem(ColorType color) {
		return getPirateHat(2, color);
	}
}
