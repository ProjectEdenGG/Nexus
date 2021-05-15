package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class WoolScarf extends TeamLoadoutPerk {
	@Override
	public String getName() {
		return "Wool Scarf";
	}

	@Override
	public @NotNull String getDescription() {
		return "Keep yourself warm and cozy with this wool scarf";
	}

	@Override
	public int getPrice() {
		return 25;
	}

	@Override
	protected Material getColorMaterial(ColorType color) {
		return color.getCarpet();
	}
}
