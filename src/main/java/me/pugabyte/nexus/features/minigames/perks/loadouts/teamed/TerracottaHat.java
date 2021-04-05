package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;

public class TerracottaHat extends TeamLoadoutPerk {
	@Override
	public String getName() {
		return "Terracotta";
	}

	@Override
	public String getDescription() {
		return "Protect your head with this uniquely colored clay";
	}

	@Override
	public int getPrice() {
		return 25;
	}

	@Override
	protected Material getColorMaterial(ColorType color) {
		return color.getTerracotta();
	}
}
