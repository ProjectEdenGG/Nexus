package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ConcretePowderHat extends TeamLoadoutPerk {
	@Override
	public String getName() {
		return "Concrete Powder";
	}

	@Override
	public @NotNull String getDescription() {
		return "Encase your head in a cube of concrete powder";
	}

	@Override
	public int getPrice() {
		return 25;
	}

	@Override
	protected Material getColorMaterial(ColorType color) {
		return color.getConcretePowder();
	}
}
