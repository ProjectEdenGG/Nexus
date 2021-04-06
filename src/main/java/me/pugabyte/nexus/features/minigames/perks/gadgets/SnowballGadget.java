package me.pugabyte.nexus.features.minigames.perks.gadgets;

import me.pugabyte.nexus.features.minigames.models.perks.common.GadgetPerk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SnowballGadget extends GadgetPerk {
	@Override
	public String getName() {
		return "Snowballs";
	}

	@Override
	public String getDescription() {
		return "An endless supply of snowballs to toss at your friends";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public ItemStack getItem() {
		return basicItem(Material.SNOWBALL);
	}

	@Override
	public boolean cancelEvent() {
		return false;
	}
}
