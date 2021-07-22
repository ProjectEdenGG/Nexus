package gg.projecteden.nexus.features.minigames.perks.loadouts.teamed.pirate;

import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TricornHat implements IPirateHat {
	@Override
	public @NotNull String getName() {
		return "Tricorn";
	}

	@Override
	public ItemStack getColorItem(ColorType color) {
		return getPirateHat(4, color);
	}
}
