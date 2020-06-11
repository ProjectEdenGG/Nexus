package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.*;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.menus.rewardchests.RewardChestLoot;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
						MysteryChest.saveConfig();
						MysteryChest.getInv(id).open(player);
					}
			));

			Pagination page = contents.pagination();

			RewardChestLoot[] loots = MysteryChest.getAllRewards();
			ClickableItem[] menuItems = new ClickableItem[loots.length];
			for (int i = 0; i < loots.length; i++) {
				int j = i;
				menuItems[i] = ClickableItem.from(new ItemBuilder(loots[i].isActive() ? Material.ENDER_CHEST : Material.CHEST)
								.name("&e" + loots[i].getTitle()).lore("&7Shift-Right Click to Delete").build(),
						e -> {
							InventoryClickEvent event = (InventoryClickEvent) e.getEvent();
							if (event.isShiftClick() && event.isRightClick()) {
								MenuUtils.confirmMenu(player, ConfirmationMenu.builder().onCancel(itemClickData -> {
									MysteryChest.getInv(null).open(player, 0);
								}).onConfirm(itemClickData -> {
									MysteryChest.getConfig().set(loots[j].getId() + "", null);
									MysteryChest.saveConfig();
									Tasks.wait(1, () -> MysteryChest.getInv(null).open(player, 0));
								}).build());
							} else {
								MysteryChest.getInv(loots[j].getId()).open(player);
							}
						});
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
			contents.set(0, 0, ClickableItem.from(backItem(), e -> {
				save(player);
				MysteryChest.getInv(null).open(player, 0);
			}));
			RewardChestLoot loot = MysteryChest.getRewardChestLoot(id);
			contents.set(0, 3, ClickableItem.from(new ItemBuilder(Material.PAPER).name("&eChange Title")
					.lore("&3Current: &e" + loot.getTitle()).build(), e -> {
				player.closeInventory();
				MenuUtils.openAnvilMenu(player,
						loot.getTitle(),
						(player1, s) -> {
							loot.setTitle(s);
							MysteryChest.getConfig().set(id + "", loot);
							MysteryChest.saveConfig();
							Tasks.wait(1, () -> MysteryChest.getInv(id).open(player1));
							return AnvilGUI.Response.close();
						},
						player1 -> {
							Tasks.wait(1, () -> MysteryChest.getInv(id).open(player1));
							AnvilGUI.Response.close();
						});
			}));
			contents.set(0, 5, ClickableItem.from(new ItemBuilder(loot.isActive() ? Material.ENDER_CHEST : Material.CHEST)
					.name("&eToggle Active").lore("&3" + loot.isActive()).build(), e -> {
				loot.setActive(!loot.isActive());
				MysteryChest.getConfig().set(id + "", loot);
				MysteryChest.saveConfig();
				Tasks.wait(1, () -> MysteryChest.getInv(id).open(player));
			}));

			contents.set(0, 8, ClickableItem.from(new ItemBuilder(Material.END_CRYSTAL).name("&eSave").build(), e -> save(player)));

			for (int i = 0; i < loot.getItems().length; i++) {
				contents.set(1, i, ClickableItem.empty(loot.getItems()[i]));
			}

			for (int i = 0; i < 9; i++) {
				contents.setEditable(SlotPos.of(1, i), true);
			}

		}
	}

	private void save(Player player) {
		int[] editableSlots = {9, 10, 11, 12, 13, 14, 15, 16, 17};
		Inventory inv = player.getOpenInventory().getTopInventory();
		List<ItemStack> items = new ArrayList<>();
		for (int i : editableSlots) {
			if (Utils.isNullOrAir(inv.getItem(i))) continue;
			items.add(inv.getItem(i));
		}
		RewardChestLoot loot = MysteryChest.getRewardChestLoot(id);
		loot.setItems(items.toArray(new ItemStack[items.size()]));
		MysteryChest.getConfig().set(id + "", loot);
		MysteryChest.saveConfig();
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
