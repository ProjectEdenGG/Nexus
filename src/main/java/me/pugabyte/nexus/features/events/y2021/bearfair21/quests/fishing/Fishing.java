package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing;

import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Fishing {

	public Fishing() {
		// TODO?
	}

	public static ItemStack getLoot() {
		FishingLoot fishingLoot = getFishingLoot();
		ItemBuilder result = new ItemBuilder(fishingLoot.getMaterial());

		if (fishingLoot.getCustomName() != null)
			result.name(fishingLoot.getCustomName());
		if (fishingLoot.getCustomModelData() != 0)
			result.customModelData(fishingLoot.getCustomModelData());

		return result.build();
	}

	private static FishingLoot getFishingLoot() {
		FishingLootCategory category = getLootCategory();
		Map<FishingLoot, Double> lootMap = new HashMap<>();
		for (FishingLoot loot : FishingLoot.of(category))
			lootMap.put(loot, loot.getWeight());

		return RandomUtils.getWeightedRandom(lootMap);
	}

	private static FishingLootCategory getLootCategory() {
		Map<FishingLootCategory, Double> categoryMap = new HashMap<>();
		for (FishingLootCategory category : FishingLootCategory.values())
			categoryMap.put(category, category.getWeight());

		return RandomUtils.getWeightedRandom(categoryMap);
	}
}
