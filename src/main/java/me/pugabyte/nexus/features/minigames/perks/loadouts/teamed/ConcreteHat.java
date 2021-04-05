package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;

public class ConcreteHat extends TeamLoadoutPerk {
	@Override
	public String getName() {
		return "Concrete";
	}

	@Override
	public String getDescription() {
		return "Protect your head with a slab of concrete!||&3Disclaimer: does not actually protect you.";
	}

	@Override
	public int getPrice() {
		return 30;
	}

	@Override
	protected Material getColorMaterial(ColorType color) {
		return color.getConcrete();
	}
}
