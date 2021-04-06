package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;

public class DragonSkull extends LoadoutPerk {
	@Override
	public String getName() {
		return "Dragon Skull";
	}

	@Override
	public String getDescription() {
		return "Scare your enemies with the frightening skull of a mighty dragon!";
	}

	@Override
	public int getPrice() {
		return 100;
	}

	@Override
	public Material getMaterial() {
		return Material.DRAGON_HEAD;
	}
}
