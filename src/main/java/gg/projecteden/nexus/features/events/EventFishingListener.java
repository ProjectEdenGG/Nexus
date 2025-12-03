package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.models.EventFishingLoot;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.FishingLoot;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingBiteEvent;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingCaughtFishEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
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
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return getFishingLoot(player, category, event.getFishingLoot());
	}

	public static FishingLoot getFishingLoot(Player player, EventFishingLootCategory category, List<FishingLoot> fishingLoot) {
		Map<FishingLoot, Double> lootMap = new HashMap<>();
		for (FishingLoot loot : fishingLoot)
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

	Map<Player, List<ItemStack>> playerCatchMap = new HashMap<>();

	// Hand == NULL on State.BITE
	@EventHandler
	public void onBite(PlayerFishEvent event) {
		State state = event.getState();
		if (state != State.BITE)
			return;

		Player player = event.getPlayer();
		if (!this.event.shouldHandle(player))
			return;

		ItemStack tool = ItemUtils.getTool(player, Material.FISHING_ROD);
		if (Nullables.isNullOrAir(tool))
			return;

		ItemMeta meta = tool.getItemMeta();

		List<ItemStack> loot = new ArrayList<>();
		loot.add(getFishingItem(player));

		if (meta != null && meta.hasEnchants()) {
			if (meta.getEnchants().keySet().stream().anyMatch(enchantment -> enchantment.equals(Enchantment.LUCK_OF_THE_SEA))) {
				int loops = RandomUtils.randomInt(0, meta.getEnchants().get(Enchantment.LUCK_OF_THE_SEA));

				for (int i = 0; i < loops; i++) {
					loot.add(getFishingItem(player));
				}
			}
		}

		PlayerEventFishingBiteEvent biteEvent = new PlayerEventFishingBiteEvent(player, loot);
		if (biteEvent.callEvent()) {
			playerCatchMap.put(player, biteEvent.getLoot());
		}

	}

	@EventHandler
	public void onHook(PlayerFishEvent event) {
		State state = event.getState();
		if (state == State.BITE)
			return;

		Player player = event.getPlayer();
		if (!this.event.shouldHandle(player))
			return;

		ItemStack tool = ItemUtils.getTool(player, Material.FISHING_ROD);
		if (Nullables.isNullOrAir(tool))
			return;

		Entity caught = event.getCaught();
		if (!(caught instanceof Item item))
			return;

		if (state == State.CAUGHT_FISH) {

			List<ItemStack> loot = playerCatchMap.remove(player);
			if (Nullables.isNullOrEmpty(loot))
				return;

			new PlayerEventFishingCaughtFishEvent(player, loot).callEvent();

			ItemStack loot0 = loot.removeFirst();
			ItemStack originalItem = item.getItemStack();
			originalItem.setType(loot0.getType());
			originalItem.setItemMeta(loot0.getItemMeta());
			originalItem.setAmount(loot0.getAmount());

			Tasks.wait(1, () -> {
				for (ItemStack itemStack : loot) {
					Item _item = player.getWorld().dropItem(item.getLocation(), itemStack);
					_item.setVelocity(item.getVelocity());
				}
			});
		} else {
			playerCatchMap.remove(player);
		}
	}

	private ItemStack getFishingItem(Player player) {
		ItemStack loot = getLoot(player);
		if (loot == null)
			loot = event.getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.CARP).getItem();

		return loot;
	}
}
