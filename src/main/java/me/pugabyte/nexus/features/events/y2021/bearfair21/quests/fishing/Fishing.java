package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;
import static me.pugabyte.nexus.utils.ItemUtils.getTool;

public class Fishing implements Listener {

	public Fishing() {
		Nexus.registerListener(this);
	}

	private static ItemStack getLoot(Player player) {
		FishingLoot fishingLoot = getFishingLoot(player);
		if (fishingLoot == null)
			return null;

		ItemBuilder result = new ItemBuilder(fishingLoot.getMaterial(player));

		if (fishingLoot.getCustomName() != null)
			result.name(fishingLoot.getCustomName());
		if (fishingLoot.getCustomModelData() != 0)
			result.customModelData(fishingLoot.getCustomModelData());

		return result.build();
	}

	private static FishingLoot getFishingLoot(Player player) {
		FishingLootCategory category = getLootCategory();
		Map<FishingLoot, Double> lootMap = new HashMap<>();
		for (FishingLoot loot : FishingLoot.of(category)) {
			if (category.equals(FishingLootCategory.UNIQUE)) {
				if (loot.timeApplies(player.getWorld()) && loot.regionApplies(player.getLocation()))
					lootMap.put(loot, loot.getWeight());
			} else
				lootMap.put(loot, loot.getWeight());
		}

		if (lootMap.isEmpty())
			return null;

		return RandomUtils.getWeightedRandom(lootMap);
	}

	private static FishingLootCategory getLootCategory() {
		Map<FishingLootCategory, Double> categoryMap = new HashMap<>();
		for (FishingLootCategory category : FishingLootCategory.values())
			categoryMap.put(category, category.getWeight());

		return RandomUtils.getWeightedRandom(categoryMap);
	}

	@EventHandler
	public void onFishCatch(PlayerFishEvent event) {
		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;

		ItemStack rod = getTool(player);
		if (rod == null) return;

		if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) return;

		Entity caught = event.getCaught();
		if (!(caught instanceof Item)) return;

		ItemStack loot = getLoot(player);
		if (loot == null) return;

		Item item = (Item) caught;
		ItemStack itemStack = item.getItemStack();
		itemStack.setType(loot.getType());
		itemStack.setItemMeta(loot.getItemMeta());
	}
}
