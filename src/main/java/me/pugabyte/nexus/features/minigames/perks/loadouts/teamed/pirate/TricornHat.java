package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate;

import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.inventory.ItemStack;

public class TricornHat extends BasePirateHat {
	@Override
	public String getName() {
		return "Tricorn";
	}

	@Override
	protected ItemStack getColorItem(ColorType color) {
		return getPirateHat(4, color);
	}
}
