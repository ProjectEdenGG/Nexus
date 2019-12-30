package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

public class MinigamesMenus extends MenuUtils {

    public void openArenaMenu(Player player, Arena arena){
        SmartInventory inv = SmartInventory.builder()
                .id("minigameManager")
                .title(Utils.colorize("&b" + arena.getDisplayName()))
                .provider(new ArenaMenu(arena))
                .size(4, 9)
                .build();
        inv.open(player);
    }

    public void openMechanicsMenu(Player player, Arena arena){
        SmartInventory INV = SmartInventory.builder()
                .id("mechanicMenu")
                .title("Game Mechanic Type")
                .size(1 + getRows(MechanicType.values().length), 9)
                .provider(new MechanicsMenu(arena))
                .build();
        INV.open(player);
    }

    public void openLobbyMenu(Player player, Arena arena){
        SmartInventory INV = SmartInventory.builder()
                .id("lobbyMenu")
                .title("Lobby Menu")
                .provider(new LobbyMenu(arena))
                .size(2, 9)
                .build();
        INV.open(player);
    }

}
