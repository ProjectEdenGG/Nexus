package gg.projecteden.nexus.features.menus.itemeditor.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.itemeditor.ItemEditMenu;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.menus.itemeditor.ItemEditorMenu.openItemEditor;

public class ItemEditorProvider extends MenuUtils implements InventoryProvider {

	private ItemEditMenu menu;

	public ItemEditorProvider(ItemEditMenu menu) {
		this.menu = menu;
	}

	private void customEnchant(Player player, String enchant, String level) {
		PlayerUtils.runCommand(player, "customenchantments:ce enchant " + enchant + " " + level);
	}

	// click event once set, it not changing
	private void handleLevelChange(Player player, InventoryContents contents, int slot, ClickType event, int row, int col, ItemStack apply, boolean vanilla, int applyRow, int applyCol) {
		int level = 1;
		ItemStack levelItem = contents.get(row, col).get().getItem();
		if (contents.get(row, col).isPresent()) {
			// Add or Remove 1 item to amount
			int amt = levelItem.getAmount();
			if(event.isLeftClick()) {
				if(amt>1) {
					level = amt - 1;
					levelItem.setAmount(level);
				}
			} else if(event.isRightClick()) {
				if(amt<64) {
					level = amt + 1;
					levelItem.setAmount(level);
				}
			}
			ItemMeta meta = levelItem.getItemMeta();
			List<String> lore = meta.getLore();
			String amount = "Level: " + level;
			lore.set(0,amount);
			meta.setLore(lore);
			levelItem.setItemMeta(meta);

			// Set the item with the new amount
			contents.set(row, col, ClickableItem.of(levelItem, e -> handleLevelChange(player, contents, slot, e.getClick(), row, col, apply, vanilla, applyRow, applyCol)));

			// Set the level lore of the apply item
			meta = apply.getItemMeta();
			lore = meta.getLore();
			amount = "Level: " + level;
			lore.set(1,amount);
			meta.setLore(lore);
			apply.setItemMeta(meta);

			contents.set(applyRow, applyCol, ClickableItem.of(apply, e -> setEnchantAndLevel(player, contents, slot, applyRow, applyCol, vanilla)));
		}
	}

	private void handleEnchantChange(Player player, InventoryContents contents, int slot, int row, int col, String enchant, ItemStack apply, boolean vanilla, int applyRow, int applyCol){
		// Set the enchant lore of the apply item
		if(contents.get(row,col).isPresent()){
			ItemMeta meta = apply.getItemMeta();
			List<String> lore = apply.getItemMeta().getLore();
			String ench = "Enchant: " + enchant.replace(" ","_");
			lore.set(0,ench);
			meta.setLore(lore);
			apply.setItemMeta(meta);

			contents.set(applyRow, applyCol, ClickableItem.of(apply, e -> setEnchantAndLevel(player, contents, slot, applyRow, applyCol, vanilla)));
		}
	}

	private void setEnchantAndLevel(Player player, InventoryContents contents, int slot, int row, int col, boolean vanilla){
		// Get lore of item, set variables accordingly, and run the command
		if (contents.get(row, col).isPresent()) {
			ItemStack applyItem = contents.get(row, col).get().getItem();
			List<String> lore = applyItem.getItemMeta().getLore();
			String enchant = lore.get(0).toLowerCase().replaceAll("enchant: ","");
			if(enchant.equals("<none>"))
				// do something?
				return;
			String level = lore.get(1).toLowerCase().replaceAll("level: ","");

			if(vanilla){
//				Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchant));
				ItemStack item = player.getInventory().getItem(slot);
//				addVanillaEnchant(player, slot, item, ench, Integer.parseInt(level));
			}else{
				customEnchant(player, enchant, level);
			}
		}
	}

	private void removeVanillaEnchant(Player player, int slot, ItemStack item, Enchantment enchant){
		item.removeEnchantment(enchant);
		player.getInventory().setItem(slot,item);
		// Refresh to get the remaining enchants on the item
		openItemEditor(player,ItemEditMenu.VANILLA_REMOVE);
	}

	public void addVanillaEnchant(Player player, int slot, ItemStack item, Enchantment enchant, int level){
		// unsafe allows for higher than normal levels
		item.addUnsafeEnchantment(enchant,level);
		player.getInventory().setItem(slot,item);
	}

	public void toggleShiny(Player player, ItemStack item, int slot){
		ItemMeta meta = item.getItemMeta();
		if(meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
			item.removeEnchantment(Enchantment.ARROW_INFINITE);
			meta = item.getItemMeta();
			meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}else{
			item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE,69);
			meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(meta);
		player.getInventory().setItem(slot, item);
	}

	public void toggleSpamClick(Player player, ItemStack item, int slot){
		// aw hell no
	}


	@Override
	public void init(Player player, InventoryContents contents) {

		switch (menu) {
			case MAIN -> contents.set(0, 0, ClickableItem.of(closeItem(), e -> contents.inventory().close(player)));
			case VANILLA_OR_CUSTOM -> contents.set(0, 0, ClickableItem.of(backItem(), e -> openItemEditor(player, ItemEditMenu.MAIN)));
			case VANILLA_ADD_REMOVE -> contents.set(0, 0, ClickableItem.of(backItem(), e -> openItemEditor(player, ItemEditMenu.VANILLA_OR_CUSTOM)));
			case VANILLA_ADD -> contents.set(0, 0, ClickableItem.of(backItem(), e -> openItemEditor(player, ItemEditMenu.VANILLA_ADD_REMOVE)));
			case VANILLA_REMOVE -> contents.set(0, 0, ClickableItem.of(backItem(), e -> openItemEditor(player, ItemEditMenu.VANILLA_ADD_REMOVE)));
			case CUSTOM_ADD_REMOVE -> contents.set(0, 0, ClickableItem.of(backItem(), e -> openItemEditor(player, ItemEditMenu.VANILLA_OR_CUSTOM)));
			case CUSTOM_ADD -> contents.set(0, 0, ClickableItem.of(backItem(), e -> openItemEditor(player, ItemEditMenu.CUSTOM_ADD_REMOVE)));
			case CUSTOM_REMOVE -> contents.set(0, 0, ClickableItem.of(backItem(), e -> openItemEditor(player, ItemEditMenu.CUSTOM_ADD_REMOVE)));
		}
		switch (menu) {
			case MAIN:
				ItemStack editName = nameItem(Material.NAME_TAG, "Edit Name");
//				ItemStack editLore = nameItem(Material.WRITABLE_BOOK, "Edit Lore");
				ItemStack toggleSpam = nameItem(Material.DIAMOND_SWORD, "Toggle Spam Click","*Disabled*");
				ItemStack editEnchants = nameItem(Material.ENCHANTED_BOOK, "Enchants");
				ItemStack toggleShiny = nameItem(Material.NETHER_STAR, "Toggle Shiny","Don't enchant a shiny||item and vice versa.");

				int slot = player.getInventory().getHeldItemSlot();
				ItemStack heldItem = player.getInventory().getItem(slot);

				// TODO: Edit Name --> via chat?
				contents.set(1, 1, ClickableItem.of(editName, e ->  openItemEditor(player, ItemEditMenu.EDIT_NAME)));
				// TODO: Edit Lore --> via chat?
//				contents.set(1, 2, ClickableItem.of(editLore, e ->  openItemEditor(player, ItemEditMenu.EDIT_LORE)));

				// TODO: Toggle Spam --> NMS, or use a library
				contents.set(1, 4, ClickableItem.of(toggleSpam, e ->  toggleSpamClick(player, heldItem, slot)));

				contents.set(1, 6, ClickableItem.of(editEnchants, e -> openItemEditor(player, ItemEditMenu.VANILLA_OR_CUSTOM)));

				contents.set(1, 7, ClickableItem.of(toggleShiny, e ->  toggleShiny(player, heldItem, slot)));
				break;
			case VANILLA_OR_CUSTOM:
				ItemStack vanilla = nameItem(Material.ENCHANTED_BOOK, "&bVanilla");
				ItemStack custom = nameItem(Material.BOOK, "&aCustom");

				contents.set(0, 3, ClickableItem.of(vanilla, e ->  openItemEditor(player, ItemEditMenu.VANILLA_ADD_REMOVE)));

				// TODO: All of Custom Enchantments
				contents.set(0, 6, ClickableItem.of(custom, e -> openItemEditor(player,ItemEditMenu.CUSTOM_ADD_REMOVE)));
				break;
			case VANILLA_ADD_REMOVE:
				ItemStack add = nameItem(Material.SLIME_BLOCK, "&aAdd Enchantment");
				ItemStack remove = nameItem(Material.TNT, "&cRemove Enchantment");

				contents.set(0, 3, ClickableItem.of(add, e -> openItemEditor(player, ItemEditMenu.VANILLA_ADD)));
				contents.set(0, 6, ClickableItem.of(remove, e -> openItemEditor(player, ItemEditMenu.VANILLA_REMOVE)));
				break;
			case VANILLA_ADD:
//				ItemStack level = nameItem(Material.EXPERIENCE_BOTTLE,"&aEnchantment level","Level: 1");
				ItemStack apply = nameItem(Material.END_CRYSTAL,"&eClick To Apply","Enchant: <none>||Level: 1");
				ItemStack enchantItem;
				slot = player.getInventory().getHeldItemSlot();
				heldItem = player.getInventory().getItem(slot);
				List<Enchantment> applicable = ItemUtils.getApplicableEnchantments(heldItem);

				int bookRow = 1;
				int bookCol = 1;
				for (Enchantment applicableEnch : applicable) {
//					String enchantName = ShowEnchants.getPrettyName(applicableEnch.getKey().getKey().toLowerCase());
//					enchantItem = nameItem(new ItemStack(Material.ENCHANTED_BOOK),enchantName);
					int finalBookRow = bookRow;
					int finalBookCol = bookCol;
//					contents.set(bookRow, bookCol, ClickableItem.of(enchantItem, e -> handleEnchantChange(player, contents, slot, finalBookRow, finalBookCol, enchantName, apply, true, 2, 7)));
					bookCol++;
					if(bookCol>5) {
						bookCol = 1;
						bookRow++;
					}
				}

//				contents.set(1, 7, ClickableItem.of(level, e -> handleLevelChange(player, contents, slot, e.getClick(), 1, 7, apply, true, 2, 7)));
				contents.set(2, 7, ClickableItem.of(apply, e -> setEnchantAndLevel(player, contents, slot, 2, 7, true)));
				break;
			case VANILLA_REMOVE:
				slot = player.getInventory().getHeldItemSlot();
				heldItem = player.getInventory().getItem(slot);
				if(heldItem.getItemMeta().hasEnchants()) {
					Map<Enchantment, Integer> enchantsMap = heldItem.getEnchantments();
					ItemStack book;
					bookRow = 1;
					bookCol = 1;
					for (Map.Entry<Enchantment, Integer> entry : enchantsMap.entrySet()) {
						Enchantment itemEnchantment = entry.getKey();
//						String enchantName = ShowEnchants.getPrettyName(entry.getKey().getKey().getKey().toLowerCase());
//						book = nameItem(new ItemStack(Material.ENCHANTED_BOOK), "&b" + enchantName,"&cClick to remove");
//						contents.set(bookRow, bookCol, ClickableItem.of(book, e -> removeVanillaEnchant(player, slot, heldItem, itemEnchantment)));
						bookCol++;
						if(bookCol>7) {
							bookCol = 1;
							bookRow++;
						}
					}
				}
				break;
		}
	}

	private void doNothing(){
		return;
	}


}
