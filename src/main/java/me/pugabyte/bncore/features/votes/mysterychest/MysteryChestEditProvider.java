package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
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
						//TODO NEW
					}
			));

			Pagination page = contents.pagination();

			RewardChestLoot[] loots = MysteryChest.getAllRewards();
			ClickableItem[] menuItems = new ClickableItem[loots.length];
			for (int i = 0; i < loots.length; i++) {
				menuItems[i] = ClickableItem.empty(new ItemBuilder(loots[i].isActive() ? Material.ENDER_CHEST : Material.CHEST)
						.name("&e" + loots[i].getTitle()).build());
			}
			page.setItems(menuItems);
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
