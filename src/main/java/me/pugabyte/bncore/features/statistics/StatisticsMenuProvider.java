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
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsMenuProvider extends MenuUtils implements InventoryProvider {

	StatisticsMenu.StatsMenus menu;
	Player targetPlayer;

	public StatisticsMenuProvider(StatisticsMenu.StatsMenus menu, Player targetPlayer) {
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
			case GENERAL:
				addBackItem(contents, e -> StatisticsMenu.open(player, StatisticsMenu.StatsMenus.MAIN, 0, targetPlayer));
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
				ItemStack item = new ItemBuilder(Material.valueOf(entity.name() + "_SPAWN_EGG"))
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


	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
