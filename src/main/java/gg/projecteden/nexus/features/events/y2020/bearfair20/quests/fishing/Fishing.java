package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing;

import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.IslandType;
import gg.projecteden.nexus.features.events.y2020.bearfair20.models.WeightedLoot;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
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

import static gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20.getWGUtils;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20.isAtBearFair;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20.isBFItem;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20.send;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.itemLore;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.toolError;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.cod;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.coral;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.crimsonfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.flathead;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.genericFish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.glacierfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.heartOfTheSea;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.midnightCarp;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.nautilusShell;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.pufferfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.salmon;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.scales;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.seaCucumber;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.sunfish;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.tigerTrout;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.trash;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing.Loot.tropicalFish;
import static gg.projecteden.nexus.utils.ItemUtils.getTool;

public class Fishing implements Listener {
	private List<ItemStack> lootList = new ArrayList<>();
	public static Set<WeightedLoot> weightedList = new HashSet<>();
	ItemStack safetyLoot = new ItemBuilder(Material.COD).lore(itemLore).build();
	static Map<UUID, Integer> safeties = new HashMap<>();

	public Fishing() {
		Nexus.registerListener(this);
		new Loot();
		addWeightedItems();
		translateWeight();
	}

	// @formatter:off
	private void addWeightedItems() {
		String mainIsland = IslandType.MAIN.get().getRegion();
		String mgnIsland = IslandType.MINIGAME_NIGHT.get().getRegion();
		String halloweenIsland = IslandType.HALLOWEEN.get().getRegion();
		String pugmasIsland = IslandType.PUGMAS.get().getRegion();
		String sduIsland = IslandType.SUMMER_DOWN_UNDER.get().getRegion();

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

//		Utils.wakka("");
//		Utils.wakka(player.getName() + "'s Possible Loot:");
//		for (ItemStack itemStack : lootList) {
//			String msg = itemStack.getType() + ": " + itemStack.getAmount();
//			Utils.wakka(" - " + msg);
//		}
//		Utils.wakka("");

		ItemStack itemStack = RandomUtils.randomElement(lootList);
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
			Set<ProtectedRegion> regions = getWGUtils().getRegionsAt(player.getLocation());
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
		if (!(caught instanceof Item item)) return;
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
