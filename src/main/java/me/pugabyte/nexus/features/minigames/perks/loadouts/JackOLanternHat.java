package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class JackOLanternHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Jack O' Lantern";
	}

	@Override
	public String getDescription() {
		return "Scare your friends with this scarecrow head";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public Material getMaterial() {
		return Material.JACK_O_LANTERN;
	}
}
