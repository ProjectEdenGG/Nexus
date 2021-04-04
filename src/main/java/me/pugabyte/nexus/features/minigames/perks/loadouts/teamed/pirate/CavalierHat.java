package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate;

import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.inventory.ItemStack;

public class CavalierHat extends BasePirateHat {
	@Override
	public String getName() {
		return "Cavalier";
	}

	@Override
	protected ItemStack getColorItem(ColorType color) {
		return getPirateHat(1, color);
	}
}
