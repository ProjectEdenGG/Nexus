package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.menus.custommenus.DeathSwapMenu;
import me.pugabyte.bncore.features.minigames.menus.custommenus.ThimbleMenu;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamEditorMenu;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamsMenu;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MinigamesMenus extends MenuUtils {

    public void openArenaMenu(Player player, Arena arena){
        SmartInventory inv = SmartInventory.builder()
                .id("minigameManager")
                .title(arena.getDisplayName())
                .provider(new ArenaMenu(arena))
                .size(6, 9)
                .build();
        inv.open(player);
    }

    public void openDeleteMenu(Player player, Arena arena){
        SmartInventory INV = SmartInventory.builder()
                .id("deleteArenaMenu")
                .title("Delete Arena?")
                .provider(new DeleteArenaMenu(arena))
                .size(3, 9)
                .build();
        INV.open(player);
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

    public void openTeamsMenu(Player player, Arena arena){
        SmartInventory INV = SmartInventory.builder()
                .id("teamsMenu")
                .title("Teams Menu")
                .provider(new TeamsMenu(arena))
                .size(2, 9)
                .build();
        INV.open(player);
    }

    public void openTeamsEditorMenu(Player player, Arena arena, Team team){
        SmartInventory INV = SmartInventory.builder()
                .id("teamEditorMenu")
                .title("Team Editor Menu")
                .provider(new TeamEditorMenu(arena, team))
                .size(6, 9)
                .build();
        INV.open(player);
    }


    public void openCustomSettingsMenu(Player player, Arena arena){
        switch(arena.getMechanicType().name().toLowerCase()){
            case "death_swap":
                SmartInventory deathswapINV = SmartInventory.builder()
                        .id("deathswapMenu")
                        .title("Death Swap Settings")
                        .provider(new DeathSwapMenu(arena))
                        .size(6, 9)
                        .build();
                deathswapINV.open(player);
                break;
            case "thimble":
                SmartInventory thimbleINV = SmartInventory.builder()
                        .id("thimbleMenu")
                        .title("Thimble Settings")
                        .provider(new ThimbleMenu(arena))
                        .size(6, 9)
                        .build();
                thimbleINV.open(player);
                break;
            default:
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
    }

}
