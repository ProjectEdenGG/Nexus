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
    String PREFIX = Utils.getPrefix("JMinigames");

    @Override
    public void init(Player player, InventoryContents contents) {
        //Close Item
        contents.set(0, 0, ClickableItem.of(closeItem(), e -> player.closeInventory()));
        //Arena Item
        contents.set(0, 4, ClickableItem.empty(nameItem(new ItemStack(Material.DIAMOND_BLOCK),
                "&b" + arena.getDisplayName(), " ||&3Arena ID: &e" + arena.getId() +
                        "||&3Mechanic: &e" + arena.getMechanicType().name().replace("_", " ") +
                        "||&3Teams: &e" + arena.getTeams().size())));

        //Arena Name Item
        contents.set(1, 2, ClickableItem.of(nameItem(new ItemStack(Material.PAPER),
                "&eArena Name", " ||&3Current Name:||&e" + arena.getName()), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        arena.setName(text);
                        ArenaManager.write(arena);
                        ArenaManager.read(arena.getName());
                        menus.openArenaMenu(player, arena);
                        return AnvilGUI.Response.text(text);
                    })
                    .text("Arena Name")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Game Mechanic Item
        contents.set(1, 4, ClickableItem.of(nameItem(new ItemStack(Material.REDSTONE),
                "&eGame Mechanic", "&7Game type of the arena|| ||&3Current Mechanic:||&e" + arena.getMechanicType().name().replace("_", " ")), e -> menus.openMechanicsMenu(player, arena)));
        //Arena Display Name Item
        contents.set(1, 6, ClickableItem.of(nameItem(new ItemStack(Material.BOOK),
                "&eDisplay Name", "&7Display name of the arena|| ||&3Current Display Name:||&e" + arena.getDisplayName()), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        arena.setDisplayName(text);
                        ArenaManager.write(arena);
                        ArenaManager.read(arena.getName());
                        menus.openArenaMenu(player, arena);
                        return AnvilGUI.Response.text(text);
                    })
                    .text("Display Name")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Minimum Players Item
        contents.set(2, 1, ClickableItem.of(nameItem(new ItemStack(Material.LEATHER_CHESTPLATE),
                "&eMinimum Players", "&7Minimum players need to||&7start the game|| ||&3Current Minimum Players:||&e" + arena.getMinPlayers()), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMinPlayers(Integer.parseInt(text));
                            ArenaManager.write(arena);
                            ArenaManager.read(arena.getName());
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for minimum players."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Minimum Players")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Maximum Players Item
        contents.set(2, 2, ClickableItem.of(nameItem(new ItemStack(Material.DIAMOND_CHESTPLATE),
                "&eMaximum Players", "&7Maximum capacity for the arena|| ||&3Current Maximum Players:||&e" + arena.getMaxPlayers()), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMaxPlayers(Integer.parseInt(text));
                            ArenaManager.write(arena);
                            ArenaManager.read(arena.getName());
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for maximum players."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Maximum Players")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Teams Menu Item
        contents.set(2, 4, ClickableItem.of(nameItem(new ItemStack(Material.WOOL),
                "&eTeams", "&7Click me to open||&7the team menu"), e -> menus.openTeamsMenu(player, arena)));
        //Respawn Location Item
        contents.set(2, 6, ClickableItem.of(nameItem(new ItemStack(Material.BED, 1, (byte) 14),
                "&eRespawn Location", "&7Location players will respawn||&7while waiting to join back|| ||" +
                        "&3Current Respawn Location:" +
                        "||&ex: " + (int) arena.getRespawnLocation().getX() +
                        "||&ey: " + (int) arena.getRespawnLocation().getY() +
                        "||&ez: " + (int) arena.getRespawnLocation().getZ()), e ->{
            arena.setRespawnLocation(player.getLocation());
            ArenaManager.write(arena);
            ArenaManager.read(arena.getName());
            menus.openArenaMenu(player, arena);
        }));
        //Game Time Item
        contents.set(2, 7, ClickableItem.of(nameItem(new ItemStack(Material.WATCH),
                "&eGame Time", "&7Time in seconds that the||&7game will run for|| ||&3Current Game Time:||&e" + arena.getSeconds()), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setSeconds(Integer.parseInt(text));
                            ArenaManager.write(arena);
                            ArenaManager.read(arena.getName());
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for game time."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Game Time (Seconds)")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Lobby Item
        contents.set(3, 1, ClickableItem.of(nameItem(new ItemStack(Material.WOOD_DOOR),
                "&eLobby", "&7Set the Lobby location||&7and wait time"), e -> menus.openLobbyMenu(player, arena)));
        //Minimum Winning Score Item
        contents.set(3, 3, ClickableItem.of(nameItem(new ItemStack(Material.CLAY_BRICK),
                "&eMinimum Winning Score", "&7Set the minimum winning score||&7Set to 0 to ignore|| ||&3Current Minimum Score:||&e" + arena.getMinWinningScore()), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMinWinningScore(Integer.parseInt(text));
                            ArenaManager.write(arena);
                            ArenaManager.read(arena.getName());
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for minimum winning score."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Minimum Winning Score")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Maximum Winning Score Item
        contents.set(3, 4, ClickableItem.of(nameItem(new ItemStack(Material.IRON_INGOT),
                "&eMaximum Winning Score", "&7Set the maximum winning score||&7Set to 0 to ignore|| ||&3Current Maximum Score:||&e" + arena.getMaxWinningScore()),e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setMaxWinningScore(Integer.parseInt(text));
                            ArenaManager.write(arena);
                            ArenaManager.read(arena.getName());
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for maximum winning score."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Maximum Winning Score")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Winning Score Item
        contents.set(3, 5, ClickableItem.of(nameItem(new ItemStack(Material.GOLD_INGOT),
                "&eWinning Score", "&7Set the score needed to win|| ||&3Current Winning Score:||&e" + arena.getWinningScore()), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.setWinningScore(Integer.parseInt(text));
                            ArenaManager.write(arena);
                            ArenaManager.read(arena.getName());
                            menus.openArenaMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for winning score."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Winning Score")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
        //Allow Late Join Toggle Item
        ItemStack lateJoinItem = nameItem(new ItemStack(Material.IRON_DOOR),
                "&eLate Join", "&7Set if players can join after||&7the game has started|| ||&3Allowed:||&e" + arena.canJoinLate());
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
            ArenaManager.write(arena);
            ArenaManager.read(arena.getName());
            menus.openArenaMenu(player, arena);
        }));
        //Spectate Location Item
        contents.set(4, 2, ClickableItem.of(nameItem(new ItemStack(Material.COMPASS),
                "&eSpectate Location", "&7Location players will teleport to||&7when spectating the game|| ||" +
                        "&3Current Spectate Location:" +
                        "||&ex: " + (int) arena.getSpectatePosition().getX() +
                        "||&ey: " + (int) arena.getSpectatePosition().getY() +
                        "||&ez: " + (int) arena.getSpectatePosition().getZ()), e ->{
            arena.setSpectatePosition(player.getLocation());
            ArenaManager.write(arena);
            ArenaManager.read(arena.getName());
            menus.openArenaMenu(player, arena);
        }));
        //Custom Settings Item
        contents.set(4, 4, ClickableItem.of(nameItem(new ItemStack(Material.BOOK_AND_QUILL),
                "&eCustom Game Mechanic Settings"), e -> { menus.openCustomSettingsMenu(player, arena); }));
        //Scoreboard Toggle Item
        ItemStack scoreboardItem = nameItem(new ItemStack(Material.SIGN),
                "&eScoreboard", "&7Set if the arena has||&7a visible scoreboard|| ||&3Current Setting:||&e" + arena.hasScoreboard());
        if(arena.hasScoreboard()){
            scoreboardItem = itemGlow(scoreboardItem);
        }
        contents.set(4, 6, ClickableItem.of(scoreboardItem, e ->{
            if(arena.hasScoreboard()){
                arena.hasScoreboard(false);
            }
            else {
                arena.hasScoreboard(true);
            }
            ArenaManager.write(arena);
            ArenaManager.read(arena.getName());
            menus.openArenaMenu(player, arena);
        }));
        //Delete Arena Item
        contents.set(5, 4, ClickableItem.of(nameItem(new ItemStack(Material.TNT),
                "&c&lDelete Arena", "&7You will need to confirm||&7deleting an arena.|| ||&7&lTHIS CANNOT BE UNDONE."), e->{
            player.closeInventory();
            Utils.async(() -> menus.openDeleteMenu(player, arena));
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
