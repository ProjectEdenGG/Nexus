package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.mainRg;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.mainRegion;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Fishing implements Listener {
	private List<ItemStack> lootList = new ArrayList<>();
	public static Set<WeightedLoot> weightedList = new HashSet<>();
	ItemStack safetyLoot = new ItemBuilder(Material.COD).lore(itemLore).build();
	static Map<UUID, Integer> safeties = new HashMap<>();

	public Fishing() {
		BNCore.registerListener(this);
		populateLoot();
	}

	private void populateLoot() {
		addWeightedItems();
		translateWeight();
	}

	private void addWeightedItems() {
		String mainIsland = mainRg + "_main";
		String mgnIsland = mainRg + "_gamelobby";
		String halloweenIsland = mainRg + "_halloween";
		String pugmasIsland = mainRg + "_pugmas";
		String sduIsland = mainRg + "_summerdownunder";

		// Default Fish (Global)
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COD).lore(itemLore).build(), 25));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).lore(itemLore).build(), 18));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.TROPICAL_FISH).lore(itemLore).build(), 16));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.PUFFERFISH).lore(itemLore).build(), 14));
		// Island Specific
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COOKED_SALMON).name("Tiger Trout").lore(itemLore).build(), 1, mgnIsland));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SEA_PICKLE).name("Sea Cucumber").lore(itemLore).build(), 1, mgnIsland));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COD).name("Glacierfish").lore(itemLore).build(), 1, pugmasIsland));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("Crimsonfish").lore(itemLore).build(), 1, halloweenIsland));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COOKED_SALMON).name("Flathead").lore(itemLore).build(), 1, sduIsland));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.TROPICAL_FISH).name("Midnight Carp").lore(itemLore).build(), 1, mainIsland, false));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.TROPICAL_FISH).name("Sunfish").lore(itemLore).build(), 1, mainIsland, true));
		// Other Fish
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COOKED_SALMON).name("Bullhead").lore(itemLore).build(), 2));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COD).name("Sturgeon").lore(itemLore).build(), 2));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COD).name("Woodskip").lore(itemLore).build(), 2));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("Void Salmon").lore(itemLore).build(), 2));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("Red Snapper").lore(itemLore).build(), 2));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("Red Mullet").lore(itemLore).build(), 2));
		// Treasures
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.BRAIN_CORAL).lore(itemLore).build(), 5));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.HORN_CORAL).lore(itemLore).build(), 5));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.TUBE_CORAL).lore(itemLore).build(), 5));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.FIRE_CORAL).lore(itemLore).build(), 5));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.BUBBLE_CORAL).lore(itemLore).build(), 5));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.PHANTOM_MEMBRANE).name("Scales").lore(itemLore).build(), 2));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.HEART_OF_THE_SEA).lore(itemLore).build(), 1));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.NAUTILUS_SHELL).lore(itemLore).build(), 1));
		// Trash
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.LEATHER_BOOTS).name("Old Boots").lore(itemLore).build(), 10));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.BOOK).name("Lost Book").lore(itemLore).build(), 10));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.WOODEN_SHOVEL).name("Rusty Spoon").lore(itemLore).build(), 10));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.MUSIC_DISC_11).name("Broken CD").lore(itemLore).build(), 10));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.PAPER).name("Soggy Newspaper").lore(itemLore).build(), 10));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.STICK).name("Driftwood").lore(itemLore).build(), 10));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.KELP).name("Seaweed").lore(itemLore).build(), 10));
	}

	private void translateWeight() {
		for (WeightedLoot weightedLoot : weightedList) {
			for (int i = 0; i < weightedLoot.getWeight(); i++) {
				lootList.add(weightedLoot.getItemStack());
			}
		}
	}

	private ItemStack getLoot(Player player) {
		if (!safeties.containsKey(player.getUniqueId()))
			safeties.put(player.getUniqueId(), 0);

		if (safeties.get(player.getUniqueId()) > 50) {
			return safetyLoot;
		}

		ItemStack itemStack = Utils.getRandomElement(lootList);
		WeightedLoot weightedLoot = null;
		for (WeightedLoot weightedItem : weightedList) {
			if (weightedItem.getItemStack().equals(itemStack)) {
				weightedLoot = weightedItem;
				break;
			}
		}

		if (weightedLoot == null) {
			return null;
		}

		boolean worldDayTime = player.getWorld().isDayTime();
		Boolean dayTimeCheck = weightedLoot.getDayTimeCheck();
		if (dayTimeCheck != null && worldDayTime != dayTimeCheck) {
			return getLoot(player);
		}

		String regionCheck = weightedLoot.getRegionCheck();
		String playerRegion = null;
		if (regionCheck != null) {
			Set<ProtectedRegion> regions = WGUtils.getRegionsAt(player.getLocation());
			for (ProtectedRegion region : regions) {
				if (region.getId().equalsIgnoreCase(regionCheck)) {
					playerRegion = region.getId();
					break;
				}
			}

			if (!regionCheck.equalsIgnoreCase(playerRegion)) {
				return getLoot(player);
			}
		}

		safeties.remove(player.getUniqueId());
		return weightedLoot.getItemStack();
	}

	@EventHandler
	public void onFishCatch(PlayerFishEvent event) {
		Player player = event.getPlayer();

		if (!event.getPlayer().getWorld().equals(BearFair20.world)) return;
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(mainRegion)) return;

		ItemStack rod = player.getInventory().getItemInMainHand();
		ItemStack offHand = player.getInventory().getItemInOffHand();
		if (!rod.getType().equals(Material.FISHING_ROD)) {
			if (!offHand.getType().equals(Material.FISHING_ROD)) {
				return;
			} else {
				rod = offHand;
			}
		}

		if (rod.getLore() == null) {
			player.sendMessage(colorize(BFQuests.fishingError));
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
			event.setCancelled(true);
			return;
		}

		if (!rod.getLore().contains(itemLore)) {
			player.sendMessage(colorize(BFQuests.fishingError));
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
			event.setCancelled(true);
			return;
		}

		if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) return;

		Entity caught = event.getCaught();
		if (!(caught instanceof Item)) return;
		Item item = (Item) caught;
		ItemStack itemStack = item.getItemStack();

		ItemStack lootItemStack = getLoot(player);
		if (lootItemStack == null) return;

		itemStack.setType(lootItemStack.getType());
		itemStack.setItemMeta(lootItemStack.getItemMeta());
//		itemStack.setLore(Collections.singletonList(itemLore));
	}

	@EventHandler
	public void onMcMMOFishing(McMMOPlayerFishingEvent event) {
		Player player = event.getPlayer();

		if (!event.getPlayer().getWorld().equals(BearFair20.world)) return;
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(mainRegion)) return;

		event.setCancelled(true);
	}

	@Getter
	public static class WeightedLoot {
		@NonNull
		ItemStack itemStack;
		@NonNull
		int weight;
		String regionCheck;
		// True = Day, False = Night, Null = Ignored
		Boolean dayTimeCheck;

		WeightedLoot(ItemStack itemStack, int weight, String regionCheck, Boolean dayTimeCheck) {
			this.itemStack = itemStack;
			this.weight = weight;
			this.regionCheck = regionCheck;
			this.dayTimeCheck = dayTimeCheck;
		}

		WeightedLoot(ItemStack itemStack, int weight) {
			this.itemStack = itemStack;
			this.weight = weight;
			this.regionCheck = null;
			this.dayTimeCheck = null;
		}

		WeightedLoot(ItemStack itemStack, int weight, String regionCheck) {
			this.itemStack = itemStack;
			this.weight = weight;
			this.regionCheck = regionCheck;
			this.dayTimeCheck = null;
		}

		WeightedLoot(ItemStack itemStack, int weight, Boolean dayTimeCheck) {
			this.itemStack = itemStack;
			this.weight = weight;
			this.regionCheck = null;
			this.dayTimeCheck = dayTimeCheck;
		}

	}
}
