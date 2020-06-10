package me.pugabyte.bncore.features.holidays.bearfair20.quests.fishing;

import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.models.WeightedLoot;
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

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.BFRg;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.isAtBearFair;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.isBFItem;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.send;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.toolError;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.fishing.Loot.*;
import static me.pugabyte.bncore.utils.Utils.getTool;

public class Fishing implements Listener {
	private List<ItemStack> lootList = new ArrayList<>();
	public static Set<WeightedLoot> weightedList = new HashSet<>();
	ItemStack safetyLoot = new ItemBuilder(Material.COD).lore(itemLore).build();
	static Map<UUID, Integer> safeties = new HashMap<>();

	public Fishing() {
		BNCore.registerListener(this);
		new Loot();
		addWeightedItems();
		translateWeight();
	}

	// @formatter:off
	private void addWeightedItems() {
		String mainIsland = BFRg + "_main";
		String mgnIsland = BFRg + "_gamelobby";
		String halloweenIsland = BFRg + "_halloween";
		String pugmasIsland = BFRg + "_pugmas";
		String sduIsland = BFRg + "_summerdownunder";

		// Default Fish (Global)
		weightedList.add(new WeightedLoot(cod,				25));
		weightedList.add(new WeightedLoot(salmon,			18));
		weightedList.add(new WeightedLoot(tropicalFish,		16));
		weightedList.add(new WeightedLoot(pufferfish,		14));

		// Island Specific
		weightedList.add(new WeightedLoot(tigerTrout,		1, mgnIsland));
		weightedList.add(new WeightedLoot(seaCucumber,		1, mgnIsland));
		weightedList.add(new WeightedLoot(glacierfish,		1, pugmasIsland));
		weightedList.add(new WeightedLoot(crimsonfish,		1, halloweenIsland));
		weightedList.add(new WeightedLoot(flathead,			1, sduIsland));
		weightedList.add(new WeightedLoot(midnightCarp,	 	1, mainIsland, false));
		weightedList.add(new WeightedLoot(sunfish,		 	1, mainIsland, true));

		// Generic Fish
		for (ItemStack genericItem : genericFish)
			weightedList.add(new WeightedLoot(genericItem, 	2));

		// Treasures
		for (ItemStack coralItem : coral)
			weightedList.add(new WeightedLoot(coralItem, 	3));
		weightedList.add(new WeightedLoot(scales,			2));
		weightedList.add(new WeightedLoot(heartOfTheSea,	1));
		weightedList.add(new WeightedLoot(nautilusShell,	1));

		// Trash
		for (ItemStack trashItem : trash)
			weightedList.add(new WeightedLoot(trashItem, 	10));
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

	// Events
	@EventHandler
	public void onFishCatch(PlayerFishEvent event) {
		Player player = event.getPlayer();

		if(!isAtBearFair(player)) return;

		ItemStack rod = getTool(player);
		if(rod == null) return;
		if(!isBFItem(rod)) {
			send(toolError, player);
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
	}

	@EventHandler
	public void onMcMMOFishing(McMMOPlayerFishingEvent event) {
		Player player = event.getPlayer();
		if(!isAtBearFair(player)) return;
		event.setCancelled(true);
	}
}
