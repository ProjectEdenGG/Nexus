package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockListMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	MinigamesMenus menus = new MinigamesMenus();

	public BlockListMenu(Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {

		Pagination page = contents.pagination();

		//Back Item
		contents.set(0, 0, ClickableItem.of(backItem(), e -> {
			menus.openArenaMenu(player, arena);
		}));

		//Add Item Item
		contents.set(5, 8, ClickableItem.of(nameItem(new ItemStack(Material.ITEM_FRAME),
				"&eAdd Item", "&3Click me with an item||&3in your hand to add it."), e -> {
			Utils.wait(2, () -> {
				if (e.getCursor().getType() == null || e.getCursor().getType() == Material.AIR) return;
				arena.getBlockList().add(e.getCursor().getType());
				ArenaManager.write(arena);
				e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
				menus.blockListMenu(arena).open(player);
			});
		}));

		//WhiteList/Blacklist Item
		if (arena.isWhitelist()) {
			contents.set(5, 7, ClickableItem.of(nameItem(new ItemStack(Material.INK_SACK, 1, ColorType.WHITE.getDyeColor().getDyeData()),
					"&eWhitelisted", "&3Click to set the block||&3list mode to blacklist."), e -> {
				arena.isWhitelist(false);
				ArenaManager.write(arena);
				menus.blockListMenu(arena).open(player, page.getPage());
			}));
		} else {
			contents.set(5, 7, ClickableItem.of(nameItem(new ItemStack(Material.INK_SACK, 1, ColorType.BLACK.getDyeColor().getDyeData()),
					"&eBlacklisted", "&3Click to set the block||&3list mode to whitelist."), e -> {
				arena.isWhitelist(true);
				ArenaManager.write(arena);
				menus.blockListMenu(arena).open(player, page.getPage());
			}));
		}

		//Materials
		List<Material> sortedList = new ArrayList<>(arena.getBlockList());
		Collections.sort(sortedList);

		ClickableItem[] clickableItems = new ClickableItem[arena.getBlockList().size()];
		for (int i = 0; i < clickableItems.length; i++) {
			clickableItems[i] = ClickableItem.of(nameItem(new ItemStack(sortedList.get(i)), "&e" + sortedList.get(i).name(), "&3Click me to remove this||&3material from the list."), e -> {
				arena.getBlockList().remove(e.getCurrentItem().getType());
				ArenaManager.write(arena);
				menus.blockListMenu(arena).open(player);
			});
		}

		page.setItems(clickableItems);
		page.setItemsPerPage(36);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isLast())
			contents.set(0, 8, ClickableItem.of(new ItemStack(nameItem(new ItemStack(Material.ARROW), "&rNext Page")), e -> menus.blockListMenu(arena).open(player, page.next().getPage())));
		if (!page.isFirst())
			contents.set(0, 7, ClickableItem.of(new ItemStack(nameItem(new ItemStack(Material.BARRIER), "&rPrevious Page")), e -> menus.blockListMenu(arena).open(player, page.previous().getPage())));

	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}

}
