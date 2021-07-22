package gg.projecteden.nexus.features.minigames.perks.loadouts.teamed.pirate;

import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BicornHat implements IPirateHat {
	@Override
	public @NotNull String getName() {
		return "Bicorn";
	}

	@Override
	public ItemStack getColorItem(ColorType color) {
		return getPirateHat(2, color);
	}
}
