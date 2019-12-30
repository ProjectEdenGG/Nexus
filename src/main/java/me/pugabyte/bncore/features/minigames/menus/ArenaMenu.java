package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
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
        //Close Item
        contents.set(0, 0, ClickableItem.of(closeItem(), e -> player.closeInventory()));

        /**TODO
         * Lore on items
         */

        //Arena Item
        contents.set(0, 4, ClickableItem.empty(nameItem(new ItemStack(Material.DIAMOND_BLOCK), "&b" + arena.getDisplayName())));
        //Arena Name Item
        contents.set(1, 2, ClickableItem.of(nameItem(new ItemStack(Material.PAPER), "&eArena Name"), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        arena.setName(text);
                        ArenaManager.updateFile(arena);
                        menus.openArenaMenu(player, arena);
                        return AnvilGUI.Response.text(text);
                    })
                    .text("Arena Name")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Game Mechanic Item
        contents.set(1, 4, ClickableItem.of(nameItem(new ItemStack(Material.REDSTONE), "&eGame Mechanic"), e -> menus.openMechanicsMenu(player, arena)));
        //Arena Display Name Item
        contents.set(1, 6, ClickableItem.of(nameItem(new ItemStack(Material.BOOK), "&eDisplay Name"), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        arena.setDisplayName(text);
                        ArenaManager.updateFile(arena);
                        menus.openArenaMenu(player, arena);
                        return AnvilGUI.Response.text(text);
                    })
                    .text("Display Name")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Minimum Players Item
        contents.set(2, 1, ClickableItem.of(nameItem(new ItemStack(Material.LEATHER_CHESTPLATE), "&eMinimum Players"), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMinPlayers(Integer.parseInt(text));
                            ArenaManager.updateFile(arena);
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize("&cYou must use an integer for minimum players."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Minimum Players")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Maximum Players Item
        contents.set(2, 2, ClickableItem.of(nameItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "&eMaximum Players"), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMaxPlayers(Integer.parseInt(text));
                            ArenaManager.updateFile(arena);
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize("&cYou must use an integer for maximum players."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Maximum Players")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Teams Menu Item - TODO
        contents.set(2, 4, ClickableItem.empty(nameItem(new ItemStack(Material.WOOL), "&eTeams")));
        //Respawn Location Item
        contents.set(2, 6, ClickableItem.of(nameItem(new ItemStack(Material.COMPASS), "&eRespawn Location"), e ->{
            arena.setRespawnLocation(player.getLocation());
            ArenaManager.updateFile(arena);
            menus.openArenaMenu(player, arena);
        }));
        //Game Time Item
        contents.set(2, 7, ClickableItem.of(nameItem(new ItemStack(Material.WATCH), "&eGame Time"), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setSeconds(Integer.parseInt(text));
                            ArenaManager.updateFile(arena);
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize("&cYou must use an integer for game time."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Game Time (Seconds)")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Lobby Location Item
        contents.set(3, 1, ClickableItem.of(nameItem(new ItemStack(Material.WOOD_DOOR), "&eLobby Location"), e -> menus.openLobbyMenu(player, arena)));
        //Minimum Winning Score Item
        contents.set(3, 3, ClickableItem.of(nameItem(new ItemStack(Material.CLAY_BRICK), "&eMinimum Winning Score"), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMinWinningScore(Integer.parseInt(text));
                            ArenaManager.updateFile(arena);
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize("&cYou must use an integer for minimum winning score."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Minimum Winning Score")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Maximum Winning Score Item
        contents.set(3, 4, ClickableItem.of(nameItem(new ItemStack(Material.IRON_INGOT), "&eMaximum Winning Score"),e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMaxWinningScore(Integer.parseInt(text));
                            ArenaManager.updateFile(arena);
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize("&cYou must use an integer for maximum winning score."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Maximum Winning Score")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Winning Score Item
        contents.set(3, 5, ClickableItem.of(nameItem(new ItemStack(Material.GOLD_INGOT), "&eWinning Score"), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setWinningScore(Integer.parseInt(text));
                            ArenaManager.updateFile(arena);
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize("&cYou must use an integer for winning score."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Winning Score")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Allow Late Join Toggle Item
        ItemStack lateJoinItem = nameItem(new ItemStack(Material.WOODEN_DOOR), "&eLate Join");
        if(arena.canJoinLate()){
            lateJoinItem = itemGlow(lateJoinItem);
        }
        contents.set(3, 7, ClickableItem.of(lateJoinItem, e ->{
            if(arena.canJoinLate()){
                arena.canJoinLate(false);
            }
            else {
                arena.canJoinLate(true);
            }
            ArenaManager.updateFile(arena);
            menus.openArenaMenu(player, arena);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
