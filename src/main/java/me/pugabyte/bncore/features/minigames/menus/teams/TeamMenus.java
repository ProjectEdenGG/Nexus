package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import org.bukkit.entity.Player;

public class TeamMenus {

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

    public void openTeamsColorMenu(Player player, Arena arena, Team team){
        SmartInventory INV = SmartInventory.builder()
                .id("teamColorMenu")
                .title("Team Color Menu")
                .provider(new TeamColorMenu(arena, team))
                .size(3, 9)
                .build();
        INV.open(player);
    }

}
