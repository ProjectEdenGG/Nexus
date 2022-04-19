package gg.projecteden.nexus.features.minigames.menus.flags;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class BlockListMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public BlockListMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Pagination page = contents.pagination();

		contents.set(0, 0, ClickableItem.of(backItem(), e -> menus.openArenaMenu(player, arena)));

		contents.set(5, 8, ClickableItem.of(new ItemBuilder(Material.ITEM_FRAME)
				.name("&eAdd Item")
				.lore("&3Click me with an item", "&3in your hand to add it."),
			e -> Tasks.wait(2, () -> {
				if (isNullOrAir(player.getItemOnCursor())) return;
				arena.getBlockList().add(player.getItemOnCursor().getType());
				player.setItemOnCursor(new ItemStack(Material.AIR));
				arena.write();
				menus.blockListMenu(arena).open(player);
			})
		));

		if (arena.isWhitelist()) {
			contents.set(5, 7, ClickableItem.of(new ItemBuilder(Material.WHITE_DYE)
					.name("&eWhitelisted")
					.lore("&3Click to set the block", "&3list mode to &eblacklist."),
				e -> {
					arena.isWhitelist(false);
					arena.write();
					menus.blockListMenu(arena).open(player, page.getPage());
				}
			));
		} else {
			contents.set(5, 7, ClickableItem.of(new ItemBuilder(Material.BLACK_DYE)
					.name("&eBlacklisted")
					.lore("&3Click to set the block", "&3list mode to &ewhitelist."),
				e -> {
					arena.isWhitelist(true);
					arena.write();
					menus.blockListMenu(arena).open(player, page.getPage());
				}
			));
		}

		arena.getBlockList().removeIf(Objects::isNull);
		arena.write();

		List<Material> sortedList = new ArrayList<>(arena.getBlockList());
		Collections.sort(sortedList);

		ClickableItem[] clickableItems = new ClickableItem[arena.getBlockList().size()];
		for (int i = 0; i < clickableItems.length; i++) {
			clickableItems[i] = ClickableItem.of(new ItemBuilder(new ItemStack(sortedList.get(i)))
					.name("&e" + sortedList.get(i).name())
					.lore("&3Click me to remove this", "&3material from the list."),
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
			contents.set(0, 8, ClickableItem.of(Material.ARROW, "&fNext Page", e ->
				menus.blockListMenu(arena).open(player, page.next().getPage())));
		if (!page.isFirst())
			contents.set(0, 7, ClickableItem.of(Material.BARRIER, "&fPrevious Page", e ->
				menus.blockListMenu(arena).open(player, page.previous().getPage())));

	}

}
