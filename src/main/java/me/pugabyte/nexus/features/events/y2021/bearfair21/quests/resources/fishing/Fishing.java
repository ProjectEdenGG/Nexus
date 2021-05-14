package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootCategory;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
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

		return fishingLoot.getItem();
	}

	public static FishingLoot getFishingLoot(Player player) {
		FishingLootCategory category = getLootCategory(player);
		Map<FishingLoot, Double> lootMap = new HashMap<>();
		for (FishingLoot loot : FishingLoot.of(category)) {
			if (category.equals(FishingLootCategory.UNIQUE)) {
				if (loot.applies(player))
					lootMap.put(loot, loot.getWeight());
			} else
				lootMap.put(loot, loot.getWeight());
		}

		if (lootMap.isEmpty())
			return null;

		return RandomUtils.getWeightedRandom(lootMap);
	}

	private static FishingLootCategory getLootCategory(Player player) {
		BearFair21UserService userService = new BearFair21UserService();
		BearFair21User user = userService.get(player);
		Map<FishingLootCategory, Double> categoryMap = new HashMap<>();
		for (FishingLootCategory category : FishingLootCategory.values()) {
			double weight = category.getWeight();
			if (category.equals(FishingLootCategory.JUNK))
				weight = user.getJunkWeight().getWeight();
			categoryMap.put(category, weight);
		}

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
		if (loot == null)
			loot = FishingLoot.CARP.getItem();

		Item item = (Item) caught;
		ItemStack itemStack = item.getItemStack();
		itemStack.setType(loot.getType());
		itemStack.setItemMeta(loot.getItemMeta());
	}
}
