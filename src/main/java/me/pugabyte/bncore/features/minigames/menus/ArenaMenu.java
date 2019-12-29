package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaMenu extends MenuUtils implements InventoryProvider {

    MinigamesMenus menus = new MinigamesMenus();
    public ArenaMenu(Arena arena){
        this.arena = arena;
    }
    Arena arena;

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(0, 0, ClickableItem.of(closeItem(), e -> player.closeInventory()));
        contents.set(0, 4, ClickableItem.empty(nameItem(new ItemStack(Material.DIAMOND_BLOCK), "&b" + arena.getDisplayName())));
        contents.set(1, 2, ClickableItem.empty(nameItem(new ItemStack(Material.PAPER), "&eArena Name")));
        contents.set(1, 4, ClickableItem.of(nameItem(new ItemStack(Material.REDSTONE), "&eGame Mechanic"), e -> menus.openMechanicsMenu(player, arena)));
        contents.set(1, 6, ClickableItem.empty(nameItem(new ItemStack(Material.BOOK), "&eDisplay Name")));
        contents.set(2, 1, ClickableItem.empty(nameItem(new ItemStack(Material.LEATHER_CHESTPLATE), "&eMinimum Players")));
        contents.set(2, 2, ClickableItem.empty(nameItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "&eMaximum Players")));
        contents.set(2, 4, ClickableItem.empty(nameItem(new ItemStack(Material.WOOL), "&eTeams")));
        contents.set(2, 6, ClickableItem.empty(nameItem(new ItemStack(Material.COMPASS), "&eRespawn Location")));
        contents.set(2, 7, ClickableItem.empty(nameItem(new ItemStack(Material.WATCH), "&eGame Time")));
        contents.set(3, 1, ClickableItem.empty(nameItem(new ItemStack(Material.WOOD_DOOR), "&eLobby Location")));
        contents.set(3, 3, ClickableItem.empty(nameItem(new ItemStack(Material.CLAY_BRICK), "&eMinimum Winning Score")));
        contents.set(3, 4, ClickableItem.empty(nameItem(new ItemStack(Material.IRON_INGOT), "&eMaximum Winning Score")));
        contents.set(3, 5, ClickableItem.empty(nameItem(new ItemStack(Material.GOLD_INGOT), "&eWinning Score")));
        contents.set(3, 7, ClickableItem.empty(nameItem(new ItemStack(Material.IRON_DOOR), "&eAllow Late Join")));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
