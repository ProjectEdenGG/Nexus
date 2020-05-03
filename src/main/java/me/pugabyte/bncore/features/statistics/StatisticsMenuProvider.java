package me.pugabyte.bncore.features.statistics;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsMenuProvider extends MenuUtils implements InventoryProvider {

	StatisticsMenu.StatsMenus menu;
	OfflinePlayer targetPlayer;

	public StatisticsMenuProvider(StatisticsMenu.StatsMenus menu, OfflinePlayer targetPlayer) {
		this.menu = menu;
		this.targetPlayer = targetPlayer;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Pagination page = contents.pagination();

		switch (menu) {
			case MAIN:
				addCloseItem(contents);
				break;
			default:
				addBackItem(contents, e -> StatisticsMenu.open(player, StatisticsMenu.StatsMenus.MAIN, 0, targetPlayer));
		}

		switch (menu) {
			case MAIN:
				ItemStack general = nameItem(Material.DIAMOND, "&3General", "&eView stats like movement,||&einteractions, and more");
				ItemStack blocks = nameItem(Material.GRASS_BLOCK, "&3Blocks", "&eView stats for blocks like||&etimes mined, placed, and crafted");
				ItemStack items = nameItem(Material.TOTEM_OF_UNDYING, "&3Items", "&eView stats for items like||&etimes crafted, used, and picked up");
				ItemStack mobs = nameItem(Material.ZOMBIE_HEAD, "&3Mobs", "&eView stats for mobs like||&etimes killed and times killed by");

				contents.set(1, 1, ClickableItem.from(general, e -> StatisticsMenu.open(player, StatisticsMenu.StatsMenus.GENERAL, 0, targetPlayer)));
				contents.set(1, 3, ClickableItem.from(blocks, e -> StatisticsMenu.open(player, StatisticsMenu.StatsMenus.BLOCKS, 0, targetPlayer)));
				contents.set(1, 5, ClickableItem.from(items, e -> StatisticsMenu.open(player, StatisticsMenu.StatsMenus.ITEMS, 0, targetPlayer)));
				contents.set(1, 7, ClickableItem.from(mobs, e -> StatisticsMenu.open(player, StatisticsMenu.StatsMenus.MOBS, 0, targetPlayer)));
				return;
			case GENERAL:
				getGeneralStats(contents);
				return;
			case BLOCKS:
				page.setItems(getBlockStats());
				break;
			case ITEMS:
				page.setItems(getItemStats());
				break;
			case MOBS:
				page.setItems(getMobStats());
				break;
		}

		page.setItemsPerPage(36);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isFirst())
			contents.set(5, 0, ClickableItem.from(nameItem(Material.ARROW, "<- Page"), e ->
					StatisticsMenu.open(player, menu, page.previous().getPage(), targetPlayer)));
		if (!page.isLast())
			contents.set(5, 8, ClickableItem.from(nameItem(Material.ARROW, "Page ->"), e ->
					StatisticsMenu.open(player, menu, page.next().getPage(), targetPlayer)));
	}

	public ClickableItem[] getBlockStats() {
		List<Material> blocks = Arrays.stream(Material.values()).filter(Material::isBlock).collect(Collectors.toList());
		LinkedHashMap<ItemStack, Integer> stats = new LinkedHashMap<>();
		List<ClickableItem> items = new ArrayList<>();

		Tasks.async(() -> {
			for (Material block : blocks) {
				if (MaterialTag.UNOBTAINABLE.isTagged(block))
					continue;
				int crafted = targetPlayer.getStatistic(Statistic.CRAFT_ITEM, block);
				int used = targetPlayer.getStatistic(Statistic.USE_ITEM, block);
				int mine = targetPlayer.getStatistic(Statistic.MINE_BLOCK, block);
				int pickup = targetPlayer.getStatistic(Statistic.PICKUP, block);
				int drop = targetPlayer.getStatistic(Statistic.DROP, block);
				int total = crafted + used + mine + pickup + drop;
				if (total > 10) {
					ItemStack item = new ItemBuilder(block)
							.name("&3" + StringUtils.camelCase(block.name().replace("_", " ")))
							.lore("&eCrafted: &3" + crafted)
							.lore("&ePlaced: &3" + used)
							.lore("&eMined: &3" + mine)
							.lore("&ePicked Up: &3" + pickup)
							.lore("&eDropped: &3" + drop)
							.build();
					stats.put(item, total);
				}
			}
		});
		stats.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> items.add(ClickableItem.empty(x.getKey())));
		ClickableItem[] clickableItems = new ClickableItem[items.size()];
		clickableItems = items.toArray(clickableItems);
		return clickableItems;
	}

	public ClickableItem[] getItemStats() {
		List<Material> items = Arrays.stream(Material.values()).filter(Material::isItem).collect(Collectors.toList());
		LinkedHashMap<ItemStack, Integer> stats = new LinkedHashMap<>();
		List<ClickableItem> menuItems = new ArrayList<>();

		Tasks.async(() -> {
			for (Material item : items) {
				if (MaterialTag.UNOBTAINABLE.isTagged(item) || item.isLegacy() || MaterialTag.SPAWN_EGGS.isTagged(item))
					continue;
				int depleted = targetPlayer.getStatistic(Statistic.BREAK_ITEM, item);
				int crafted = targetPlayer.getStatistic(Statistic.CRAFT_ITEM, item);
				int used = targetPlayer.getStatistic(Statistic.USE_ITEM, item);
				int pickup = targetPlayer.getStatistic(Statistic.PICKUP, item);
				int drop = targetPlayer.getStatistic(Statistic.DROP, item);
				int total = depleted + crafted + used + pickup + drop;
				if (total > 10) {
					ItemStack menuItem = new ItemBuilder(item)
							.name("&3" + StringUtils.camelCase(item.name().replace("_", " ")))
							.lore("&eDepleted: &3" + depleted)
							.lore("&eCrafted: &3" + crafted)
							.lore("&eUsed: &3" + used)
							.lore("&ePicked Up: &3" + pickup)
							.lore("&eDropped: &3" + drop)
							.build();
					stats.put(menuItem, total);
				}
			}
		});
		stats.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> menuItems.add(ClickableItem.empty(x.getKey())));
		ClickableItem[] clickableItems = new ClickableItem[menuItems.size()];
		clickableItems = menuItems.toArray(clickableItems);
		return clickableItems;
	}

	public ClickableItem[] getMobStats() {
		List<EntityType> entities = Arrays.stream(EntityType.values()).filter(EntityType::isAlive).collect(Collectors.toList());
		LinkedHashMap<ItemStack, Integer> stats = new LinkedHashMap<>();
		List<ClickableItem> items = new ArrayList<>();

		entities.forEach(entity -> {
			if (entity.equals(EntityType.PLAYER))
				return;
			int killed = targetPlayer.getStatistic(Statistic.KILL_ENTITY, entity);
			int killedBy = targetPlayer.getStatistic(Statistic.ENTITY_KILLED_BY, entity);
			int total = killed + killedBy;

			if (total > 1) {
				Material material;
				if (entity.equals(EntityType.PIG_ZOMBIE))
					material = Material.ZOMBIE_PIGMAN_SPAWN_EGG;
				else
					material = Material.valueOf(entity.name() + "_SPAWN_EGG");
				ItemStack item = new ItemBuilder(material)
						.name("&3" + StringUtils.camelCase(entity.name().replace("_", " ")))
						.lore("&eKilled: &3" + killed)
						.lore("&eKilled By: &3" + killedBy)
						.build();
				stats.put(item, total);
			}
		});
		stats.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> items.add(ClickableItem.empty(x.getKey())));
		ClickableItem[] clickableItems = new ClickableItem[items.size()];
		clickableItems = items.toArray(clickableItems);
		return clickableItems;
	}

	public void getGeneralStats(InventoryContents contents) {
		ItemStack interactions = new ItemBuilder(Material.CRAFTING_TABLE)
				.name("&3Interactions")
				.lore("&eBeacon: &3" + targetPlayer.getStatistic(Statistic.BEACON_INTERACTION))
				.lore("&eBlast Furnace: &3" + targetPlayer.getStatistic(Statistic.INTERACT_WITH_BLAST_FURNACE))
				.lore("&eBrewing Stand: &3" + targetPlayer.getStatistic(Statistic.BREWINGSTAND_INTERACTION))
				.lore("&eCampfire: &3" + targetPlayer.getStatistic(Statistic.INTERACT_WITH_CAMPFIRE))
				.lore("&eCartography Table: &3" + targetPlayer.getStatistic(Statistic.INTERACT_WITH_CARTOGRAPHY_TABLE))
				.lore("&eCrafting Table: &3" + targetPlayer.getStatistic(Statistic.CRAFTING_TABLE_INTERACTION))
				.lore("&eFurnace: &3" + targetPlayer.getStatistic(Statistic.FURNACE_INTERACTION))
				.lore("&eLectern: &3" + targetPlayer.getStatistic(Statistic.INTERACT_WITH_LECTERN))
				//1.16 .lore("&eSmithing Table: &3" + targetPlayer.getStatistic(INTERACT_WITH_SMITHING_TABLE))
				.lore("&eSmoker: &3:" + targetPlayer.getStatistic(Statistic.INTERACT_WITH_SMOKER))
				.lore("&eStonecutter: &3" + targetPlayer.getStatistic(Statistic.INTERACT_WITH_STONECUTTER))
				.build();

		ItemStack inventories = new ItemBuilder(Material.CHEST)
				.name("&3Inventories")
				.lore("&eBarrels: &3" + targetPlayer.getStatistic(Statistic.OPEN_BARREL))
				.lore("&eBells: &3" + targetPlayer.getStatistic(Statistic.BELL_RING))
				.lore("&eChests: &3" + targetPlayer.getStatistic(Statistic.CHEST_OPENED))
				.lore("&eDispensers: &3" + targetPlayer.getStatistic(Statistic.DISPENSER_INSPECTED))
				.lore("&eDroppers: &3" + targetPlayer.getStatistic(Statistic.DROPPER_INSPECTED))
				.lore("&eEnder Chests: &3" + targetPlayer.getStatistic(Statistic.ENDERCHEST_OPENED))
				.lore("&eHoppers: &3" + targetPlayer.getStatistic(Statistic.HOPPER_INSPECTED))
				.lore("&eShulker Boxes: &3" + targetPlayer.getStatistic(Statistic.SHULKER_BOX_OPENED))
				.build();

		ItemStack distance = new ItemBuilder(Material.LEATHER_BOOTS)
				.name("&3Distance")
				.lore("&eJumps: &3" + targetPlayer.getStatistic(Statistic.JUMP))
				.lore("&eClimbed: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.CLIMB_ONE_CM)))
				.lore("&eCrouched: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.CROUCH_ONE_CM)))
				.lore("&eFallen: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.FALL_ONE_CM)))
				.lore("&eFlown: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.FLY_ONE_CM)))
				.lore("&eSprinted: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.SPRINT_ONE_CM)))
				.lore("&eSwam: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.SWIM_ONE_CM)))
				.lore("&eWalked: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.WALK_ONE_CM)))
				.lore("&eWalked on Water: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.WALK_ON_WATER_ONE_CM)))
				.lore("&eWalked under Water: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM)))
				.build();

		ItemStack movement = new ItemBuilder(Material.SADDLE)
				.name("&3Movement")
				.lore("&eBoat: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.BOAT_ONE_CM)))
				.lore("&eElytra: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.AVIATE_ONE_CM)))
				.lore("&eHorse: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.HORSE_ONE_CM)))
				.lore("&eMinecart: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.MINECART_ONE_CM)))
				.lore("&ePig: &3" + StringUtils.distanceMetricFormat(targetPlayer.getStatistic(Statistic.PIG_ONE_CM)))
				.build();

		ItemStack noteblocks = new ItemBuilder(Material.NOTE_BLOCK)
				.name("&3Music")
				.lore("&eMusic Discs Played: &3" + targetPlayer.getStatistic(Statistic.RECORD_PLAYED))
				.lore("&eNote Blocks Played: &3" + targetPlayer.getStatistic(Statistic.NOTEBLOCK_PLAYED))
				.lore("&eNote Blocks Tuned: &3" + targetPlayer.getStatistic(Statistic.NOTEBLOCK_TUNED))
				.build();

		ItemStack deathsDamage = new ItemBuilder(Material.TOTEM_OF_UNDYING)
				.name("&eDeaths and Damage")
				.lore("&eDeaths: &3" + targetPlayer.getStatistic(Statistic.DEATHS))
				.lore("&eTime Since Last Death: &3" + StringUtils.timespanFormat(targetPlayer.getStatistic(Statistic.TIME_SINCE_DEATH) / 20))
				.lore("&eDamage Taken: &3" + targetPlayer.getStatistic(Statistic.DAMAGE_TAKEN))
				.lore("&eDamage Resisted: &3" + targetPlayer.getStatistic(Statistic.DAMAGE_RESISTED))
				.lore("&eDamage Absorbed: &3" + targetPlayer.getStatistic(Statistic.DAMAGE_ABSORBED))
				.lore("&eDamaged Blocked by Shield: &3" + targetPlayer.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD))
				.lore("&eDamage Dealt: &3" + targetPlayer.getStatistic(Statistic.DAMAGE_DEALT))
				.lore("&ePlayer Kills: &3" + targetPlayer.getStatistic(Statistic.PLAYER_KILLS))
				.build();

		ItemStack times = new ItemBuilder(Material.CLOCK)
				.name("&3Times")
				.lore("&ePlayed: &3" + StringUtils.timespanFormat(targetPlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20))
				.lore("&eSince Last Rest: &3" + StringUtils.timespanFormat(targetPlayer.getStatistic(Statistic.TIME_SINCE_REST) / 20))
				.lore("&eSlept in Bed: &3" + targetPlayer.getStatistic(Statistic.SLEEP_IN_BED))
				.build();

		ItemStack items = new ItemBuilder(Material.DIAMOND)
				.name("&3Items")
				.lore("&eDropped: &3" + targetPlayer.getStatistic(Statistic.DROP_COUNT))
				.lore("&eEnchanted: &3" + targetPlayer.getStatistic(Statistic.ITEM_ENCHANTED))
				.build();

		ItemStack mobs = new ItemBuilder(Material.ZOMBIE_HEAD)
				.name("&3Mobs")
				.lore("&eAnimals Bred: &3" + targetPlayer.getStatistic(Statistic.ANIMALS_BRED))
				.lore("&eFish Caught: &3" + targetPlayer.getStatistic(Statistic.FISH_CAUGHT))
				.lore("&eMob Kills: &3" + targetPlayer.getStatistic(Statistic.MOB_KILLS))
				.lore("&eRaids Triggered: &3" + targetPlayer.getStatistic(Statistic.RAID_TRIGGER))
				.lore("&eRaids Won: &3" + targetPlayer.getStatistic(Statistic.RAID_WIN))
				.lore("&eTalked to Villager: &3" + targetPlayer.getStatistic(Statistic.TALKED_TO_VILLAGER))
				.lore("&eTraded with Villager: &3" + targetPlayer.getStatistic(Statistic.TRADED_WITH_VILLAGER))
				.build();

		ItemStack misc = new ItemBuilder(Material.CAKE)
				.name("&3Misc")
				.lore("&eCake Slices Eaten: &3" + targetPlayer.getStatistic(Statistic.CAKE_SLICES_EATEN))
				.lore("&eGames Quit: &3" + targetPlayer.getStatistic(Statistic.LEAVE_GAME))
				.lore("&ePlants Potted: &3" + targetPlayer.getStatistic(Statistic.FLOWER_POTTED))
				.build();

		contents.set(1, 1, ClickableItem.empty(distance));
		contents.set(1, 3, ClickableItem.empty(movement));
		contents.set(1, 5, ClickableItem.empty(times));
		contents.set(1, 7, ClickableItem.empty(deathsDamage));
		contents.set(2, 2, ClickableItem.empty(items));
		contents.set(2, 4, ClickableItem.empty(mobs));
		contents.set(2, 6, ClickableItem.empty(noteblocks));
		contents.set(3, 3, ClickableItem.empty(interactions));
		contents.set(3, 5, ClickableItem.empty(inventories));
		contents.set(4, 4, ClickableItem.empty(misc));
	}


	@Override
	public void update(Player player, InventoryContents contents) {

	}
}
