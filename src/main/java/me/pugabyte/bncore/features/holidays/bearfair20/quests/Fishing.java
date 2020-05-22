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
import java.util.Collections;
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
	List<ItemStack> lootList = new ArrayList<>();
	Set<WeightedLoot> weightedList = new HashSet<>();
	ItemStack safetyLoot = new ItemBuilder(Material.COD).lore(itemLore).build();
	static Map<UUID, Integer> safeties = new HashMap<>();

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

	public Fishing() {
		BNCore.registerListener(this);
		populateLoot();
	}

	private void populateLoot() {
		addWeightedItems();
		translateWeight();
	}

	private void addWeightedItems() {
		String mainIslandRg = mainRg + "_main";
		String mgnRg = mainRg + "_gamelobby";
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.COD).name("GLOBAL").build(), 1));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("GLOBAL - NIGHT ONLY").build(), 1, false));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("GLOBAL - DAY ONLY").build(), 1, true));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("MAIN - DAY ONLY").build(), 1, mainIslandRg, true));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("MAIN - NIGHT ONLY").build(), 1, mainIslandRg, false));
		weightedList.add(new WeightedLoot(new ItemBuilder(Material.SALMON).name("MGN - DAY ONLY").build(), 1, mgnRg, true));
	}

	private void translateWeight() {
		for (WeightedLoot weightedLoot : weightedList) {
			lootList.add(weightedLoot.getItemStack());
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
		itemStack.setLore(Collections.singletonList(itemLore));
	}

	@EventHandler
	public void onMcMMOFishing(McMMOPlayerFishingEvent event) {
		Player player = event.getPlayer();

		if (!event.getPlayer().getWorld().equals(BearFair20.world)) return;
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(mainRegion)) return;

		event.setCancelled(true);
	}
}
