package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamEditorMenu extends MenuUtils implements InventoryProvider {

    Arena arena;
    Team team;
    MinigamesMenus menus = new MinigamesMenus();
    public TeamEditorMenu(Arena arena, Team team){
        this.arena = arena;
        this.team = team;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        //Back Item
        contents.set(0, 0, ClickableItem.of(backItem(), e->menus.openTeamsMenu(player, arena)));

        //Name Item
        contents.set(1, 2, ClickableItem.of(nameItem(new ItemStack(Material.BOOK),
                "&eTeam Name", " ||&3Current Name:||&e" + team.getName()), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(p -> menus.openTeamsEditorMenu(player, arena, team))
                    .onComplete((p, text) -> {
                        List<Team> teams = new ArrayList<>(arena.getTeams());
                        teams.remove(team);
                        team.setName(text);
                        teams.add(team);
                        arena.setTeams(teams);
                        ArenaManager.write(arena);
                        ArenaManager.add(arena);
                        menus.openTeamsEditorMenu(player, arena, team);
                        return AnvilGUI.Response.text(text);
                    })
                    .text("Team Name")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
