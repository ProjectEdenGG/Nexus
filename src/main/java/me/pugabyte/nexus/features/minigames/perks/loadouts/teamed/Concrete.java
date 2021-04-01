package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Concrete extends TeamLoadoutPerk {
	@Override
	public String getName() {
		return "Concrete";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.CYAN_CONCRETE);
	}

	@Override
	public String[] getDescription() {
		return new String[]{"Protect your head with",
							"a slab of concrete!",
							"&oDisclaimer: does not",
							"&oactually protect you."};
	}

	@Override
	public int getPrice() {
		return 1;
	}

	@Override
	protected Material getColorMaterial(ColorType color) {
		return color.getConcrete();
	}
}
