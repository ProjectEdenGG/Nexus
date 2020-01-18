package me.pugabyte.bncore.features.minigames.menus.flags;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class BlockListMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public BlockListMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Pagination page = contents.pagination();

		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		contents.set(5, 8, ClickableItem.from(nameItem(
				Material.ITEM_FRAME,
				"&eAdd Item",
				"&3Click me with an item||&3in your hand to add it."
			),
			e -> Utils.wait(2, () -> {
				if (Utils.isNullOrAir(player.getItemOnCursor())) return;
				arena.getBlockList().add(player.getItemOnCursor().getType());
				player.setItemOnCursor(new ItemStack(Material.AIR));
				arena.write();
				menus.blockListMenu(arena).open(player);
			})
		));

		if (arena.isWhitelist()) {
			contents.set(5, 7, ClickableItem.from(nameItem(
					new ItemStack(Material.INK_SACK, 1, ColorType.WHITE.getDyeColor().getDyeData()),
					"&eWhitelisted",
					"&3Click to set the block||&3list mode to &eblacklist."
				),
				e -> {
					arena.isWhitelist(false);
					arena.write();
					menus.blockListMenu(arena).open(player, page.getPage());
				}
			));
		} else {
			contents.set(5, 7, ClickableItem.from(nameItem(
					new ItemStack(Material.INK_SACK, 1, ColorType.BLACK.getDyeColor().getDyeData()),
					"&eBlacklisted",
					"&3Click to set the block||&3list mode to &ewhitelist."
				),
				e -> {
					arena.isWhitelist(true);
					arena.write();
					menus.blockListMenu(arena).open(player, page.getPage());
				}
			));
		}

		List<Material> sortedList = new ArrayList<>(arena.getBlockList());
		Collections.sort(sortedList);

		ClickableItem[] clickableItems = new ClickableItem[arena.getBlockList().size()];
		for (int i = 0; i < clickableItems.length; i++) {
			clickableItems[i] = ClickableItem.from(nameItem(
					new ItemStack(sortedList.get(i)),
					"&e" + sortedList.get(i).name(),
					"&3Click me to remove this||&3material from the list."
					),
					e -> {
				arena.getBlockList().remove(((InventoryClickEvent) e.getEvent()).getCurrentItem().getType());
				arena.write();
				menus.blockListMenu(arena).open(player);
			});
		}

		page.setItems(clickableItems);
		page.setItemsPerPage(36);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isLast())
			contents.set(0, 8, ClickableItem.from(new ItemStack(nameItem(Material.ARROW, "&rNext Page")),
					e -> menus.blockListMenu(arena).open(player, page.next().getPage())));
		if (!page.isFirst())
			contents.set(0, 7, ClickableItem.from(new ItemStack(nameItem(Material.BARRIER, "&rPrevious Page")),
					e -> menus.blockListMenu(arena).open(player, page.previous().getPage())));

	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}

}
