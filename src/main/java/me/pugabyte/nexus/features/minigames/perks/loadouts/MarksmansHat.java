package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MarksmansHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Marksman's Hat";
	}

	@Override
	public @NotNull String getDescription() {
		return "Shoot your targets with uncanny accuracy with this hat";
	}

	@Override
	public int getPrice() {
		return 20;
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder(Material.STONE_BUTTON).customModelData(21).name("&f"+getName()).build();
	}
}
