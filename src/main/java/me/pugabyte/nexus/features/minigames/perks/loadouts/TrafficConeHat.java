package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TrafficConeHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Traffic Cone";
	}

	@Override
	public @NotNull String getDescription() {
		return "Warn others of ongoing construction with this flashy hat";
	}

	@Override
	public int getPrice() {
		return 25;
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder(Material.ORANGE_CONCRETE).customModelData(3).name("&3"+getName()).build();
	}
}
