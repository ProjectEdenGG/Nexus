package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UnicornHorn extends LoadoutPerk {
	@Override
	public Material getMaterial() {
		return Material.END_ROD;
	}

	@Override
	public String getName() {
		return "Unicorn Horn";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.END_ROD);
	}

	@Override
	public String[] getDescription() {
		return new String[]{"Become a pretty unicorn",
		                    "with this glowing horn",
				            "on top of your head!"};
	}

	@Override
	public int getPrice() {
		return 1;
	}
}
