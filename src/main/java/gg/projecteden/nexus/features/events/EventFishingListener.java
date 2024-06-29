package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.FishingLoot;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.CARP;
import static gg.projecteden.nexus.utils.ItemUtils.getTool;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class EventFishingListener implements Listener {
	private final EdenEvent event;

	public EventFishingListener(EdenEvent event) {
		this.event = event;
		Nexus.registerListener(this);
	}

	private ItemStack getLoot(Player player) {
		FishingLoot fishingLoot = getFishingLoot(player);
		if (fishingLoot == null)
			return null;

		return fishingLoot.getItem();
	}

	public FishingLoot getFishingLoot(Player player) {
		EventFishingLootCategory category = getLootCategory(player);
		Map<FishingLoot, Double> lootMap = new HashMap<>();
		for (FishingLoot loot : event.getFishingLoot())
			if (loot.getCategory() == category)
				if (loot.applies(player))
					lootMap.put(loot, loot.getWeight());

		if (lootMap.isEmpty())
			return null;

		return RandomUtils.getWeightedRandom(lootMap);
	}

	private static EventFishingLootCategory getLootCategory(Player player) {
		Map<EventFishingLootCategory, Double> categoryMap = new HashMap<>();
		for (EventFishingLootCategory category : EventFishingLootCategory.values()) {
			double weight = category.getWeight();
			categoryMap.put(category, weight);
		}

		return RandomUtils.getWeightedRandom(categoryMap);
	}

	@EventHandler
	public void onFishCatch(PlayerFishEvent event) {
		Player player = event.getPlayer();
		if (!this.event.shouldHandle(player))
			return;

		ItemStack rod = getTool(player);
		if (rod == null)
			return;

		if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
			return;

		Entity caught = event.getCaught();
		if (!(caught instanceof Item item))
			return;

		ItemStack tool = ItemUtils.getTool(player, Material.FISHING_ROD);
		if (isNullOrAir(tool))
			return;

		ItemMeta meta = tool.getItemMeta();

		int loops = 0;
		if (meta != null && meta.hasEnchants()) {
			if (meta.getEnchants().keySet().stream().anyMatch(enchantment -> enchantment.equals(Enchantment.LUCK))) {
				loops = RandomUtils.randomInt(0, meta.getEnchants().get(Enchantment.LUCK));
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
			loot = event.getFishingLoot(CARP).getItem();

		return loot;
	}
}
