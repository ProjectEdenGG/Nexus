package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25GiftGiver;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum Pugmas25GiftGiverReward {
	TIER_1(4, List.of(
		new ItemBuilder(Material.COAL)
	)),
	TIER_2(9, List.of(
		new ItemBuilder(Material.IRON_INGOT)
	)),
	TIER_3(14, List.of(
		new ItemBuilder(Material.GOLD_INGOT)
	)),
	TIER_4(19, List.of(
		new ItemBuilder(Material.DIAMOND)
	)),
	TIER_5(24, List.of(
		new ItemBuilder(Material.EMERALD)
	)),
	TIER_6(List.of(
		new ItemBuilder(Material.NETHERITE_INGOT)
	)),
	;

	private final int max;
	private final List<ItemBuilder> items;

	Pugmas25GiftGiverReward(List<ItemBuilder> items) {
		this.max = Integer.MAX_VALUE;
		this.items = items;
	}

	Pugmas25GiftGiverReward(int max, List<ItemBuilder> items) {
		this.max = max;
		this.items = items;
	}

	public static Pugmas25GiftGiverReward of(ItemStack item) {
		return of(Pugmas25GiftGiver.getPlayerHistory(item).size());
	}

	public static Pugmas25GiftGiverReward of(int timesGifted) {
		for (Pugmas25GiftGiverReward reward : values())
			if (timesGifted <= reward.max)
				return reward;
		return null;
	}

	public List<ItemStack> getItems() {
		return items.stream().map(ItemBuilder::build).toList();
	}
}

