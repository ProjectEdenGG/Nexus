package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate;

import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BicornSideHat implements IPirateHat {
	@Override
	public @NotNull String getName() {
		return "Bicorn Side";
	}

	@Override
	public ItemStack getColorItem(ColorType color) {
		return getPirateHat(3, color);
	}
}
