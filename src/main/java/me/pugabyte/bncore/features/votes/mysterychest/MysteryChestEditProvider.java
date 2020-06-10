package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.menus.rewardchests.RewardChestLoot;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MysteryChestEditProvider extends MenuUtils implements InventoryProvider {

	Integer id;

	public MysteryChestEditProvider(Integer id) {
		this.id = id;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (id == null) {
			addCloseItem(contents);

			contents.set(0, 4, ClickableItem.from(
					new ItemBuilder(Material.EMERALD_BLOCK).name("&aCreate New").build(), e -> {
						int id = MysteryChest.getNextId();
						MysteryChest.getConfig().set(id + "", new RewardChestLoot());
						MysteryChest.saveFile();
						MysteryChest.getInv(id).open(player);
					}
			));

			Pagination page = contents.pagination();

			RewardChestLoot[] loots = MysteryChest.getAllRewards();
			ClickableItem[] menuItems = new ClickableItem[loots.length];
			for (int i = 0; i < loots.length; i++) {
				int j = i;
				menuItems[i] = ClickableItem.from(new ItemBuilder(loots[i].isActive() ? Material.ENDER_CHEST : Material.CHEST)
								.name("&e" + loots[i].getTitle()).build(),
						e -> MysteryChest.getInv(loots[j].getId()).open(player));
			}
			page.setItems(menuItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isFirst())
				contents.set(5, 0, ClickableItem.from(new ItemBuilder(Material.ARROW).name("<-- Back").build(),
						e -> MysteryChest.getInv(null).open(player, page.previous().getPage())));
			if (!page.isLast())
				contents.set(5, 8, ClickableItem.from(new ItemBuilder(Material.ARROW).name("Next -->").build(),
						e -> MysteryChest.getInv(null).open(player, page.next().getPage())));
		} else {
			addBackItem(contents, e -> MysteryChest.getInv(null).open(player, 0));

		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
