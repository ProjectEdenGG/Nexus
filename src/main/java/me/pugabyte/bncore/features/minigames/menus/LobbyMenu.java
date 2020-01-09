package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Lobby;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LobbyMenu extends MenuUtils implements InventoryProvider {

    Arena arena;
    public LobbyMenu(Arena arena){
        this.arena = arena;
    }

    MinigamesMenus menus = new MinigamesMenus();
    @Override
    public void init(Player player, InventoryContents contents) {
        //Back Item
        contents.set(0, 0, ClickableItem.of(backItem(), e -> menus.openArenaMenu(player, arena)));
        //Location Item
        contents.set(1, 2, ClickableItem.of(nameItem(new ItemStack(Material.WOOD_DOOR),
                "&eLobby Location","&3Current Lobby Location:" +
                "||&ex: " + (int) arena.getLobby().getLocation().getX() +
                "||&ey: " + (int) arena.getLobby().getLocation().getY() +
                "||&ez: " + (int) arena.getLobby().getLocation().getZ()), e ->{
            arena.getLobby().setLocation(player.getLocation());
            ArenaManager.write(arena);
            ArenaManager.add(arena);
            menus.openLobbyMenu(player, arena);
        }));
        //Time Item
        contents.set(1, 6, ClickableItem.of(nameItem(new ItemStack(Material.WATCH),
                "&eWait Time", "&3Current Wait Time:||&e" + arena.getLobby().getWaitTime()), e ->{
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> { menus.openArenaMenu(player, arena); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            arena.getLobby().setWaitTime(Integer.parseInt(text));
                            ArenaManager.write(arena);
                            ArenaManager.add(arena);
                            menus.openLobbyMenu(player, arena);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(Utils.getPrefix("Minigames") + "You must use an integer for wait time."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Wait Time")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
