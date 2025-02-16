package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21Quests;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.BearFair21FishingLoot.FishingLootCategory;
import gg.projecteden.nexus.models.bearfair21.BearFair21User;
import gg.projecteden.nexus.models.bearfair21.BearFair21UserService;
import gg.projecteden.nexus.utils.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BearFair21Fishing implements Listener {

	public BearFair21Fishing() {
		Nexus.registerListener(this);
	}

	private static ItemStack getLoot(Player player) {
		BearFair21FishingLoot fishingLoot = getFishingLoot(player);
		if (fishingLoot == null)
			return null;

		return fishingLoot.getItem();
	}

	public static BearFair21FishingLoot getFishingLoot(Player player) {
		FishingLootCategory category = getLootCategory(player);
		Map<BearFair21FishingLoot, Double> lootMap = new HashMap<>();
		for (BearFair21FishingLoot loot : BearFair21FishingLoot.of(category)) {
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
		if (BearFair21.isNotAtBearFair(player)) return;

		ItemStack rod = ItemUtils.getTool(player);
		if (rod == null) return;

		if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) return;

		Entity caught = event.getCaught();
		if (!(caught instanceof Item item)) return;

		ItemStack tool = ItemUtils.getTool(player, Material.FISHING_ROD);
		if (Nullables.isNullOrAir(tool))
			return;

		ItemMeta meta = tool.getItemMeta();

		int loops = 0;
		if (meta != null && meta.hasEnchants()) {
			if (meta.getEnchants().keySet().stream().anyMatch(enchantment -> enchantment.equals(Enchantment.LUCK_OF_THE_SEA))) {
				loops = RandomUtils.randomInt(0, meta.getEnchants().get(Enchantment.LUCK_OF_THE_SEA));
			}
		}

		ItemStack loot = getFishingItem(player);
		ItemStack itemStack = item.getItemStack();
		itemStack.setType(loot.getType());
		itemStack.setItemMeta(loot.getItemMeta());

		for (int i = 0; i < loops; i++) {
			Tasks.wait(1, () -> {
				Item _item = player.getWorld().dropItem(item.getLocation(), getFishingItem(player));
				_item.setVelocity(item.getVelocity());
			});
		}
	}

	private ItemStack getFishingItem(Player player) {
		ItemStack loot = getLoot(player);
		if (loot == null)
			loot = BearFair21FishingLoot.CARP.getItem();

		return loot;
	}

	@EventHandler
	public void onOpenTreasureChest(PlayerInteractEvent event) {
		if (BearFair21.isNotAtBearFair(event)) return;
		Player player = event.getPlayer();

		ItemStack item = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(item)) return;
		if (!ItemUtils.isFuzzyMatch(item, BearFair21FishingLoot.TREASURE_CHEST.getItem())) return;

		item.setAmount(item.getAmount() - 1);
		BearFair21Quests.giveItem(player, getTreasureChestLoot());
	}

	private static ItemStack getTreasureChestLoot() {
		List<ItemStack> treasure = Arrays.asList(
			BearFair21FishingLoot.TREASURE_CHEST.getItem(),
			BearFair21FishingLoot.UNBREAKING.getItem(),
			BearFair21FishingLoot.EFFICIENCY.getItem(),
			BearFair21FishingLoot.LURE.getItem(),
			BearFair21FishingLoot.FORTUNE.getItem(),
			new ItemBuilder(Material.GOLD_NUGGET).amount(RandomUtils.randomInt(3, 7)).build(),
			new ItemBuilder(Material.GOLD_INGOT).amount(RandomUtils.randomInt(2, 5)).build(),
			new ItemBuilder(Material.IRON_INGOT).amount(RandomUtils.randomInt(2, 5)).build(),
			new ItemBuilder(Material.DIAMOND).amount(RandomUtils.randomInt(1, 3)).build()
		);

		return RandomUtils.randomElement(treasure);
	}
}
