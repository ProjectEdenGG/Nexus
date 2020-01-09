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
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DeleteTeamMenu extends MenuUtils implements InventoryProvider {

    Arena arena;
    Team team;
    TeamMenus menus = new TeamMenus();
    public DeleteTeamMenu(Arena arena, Team team){
        this.arena = arena;
        this.team = team;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRect(0, 0, 2, 8, ClickableItem.of(nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
                "&7Cancel"), e -> menus.openTeamsMenu(player, arena)));
        contents.fillRect(1, 1, 1, 7, ClickableItem.of(nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1,(byte) 5),
                "&7Cancel"), e -> menus.openTeamsMenu(player, arena)));
        contents.set(1, 4, ClickableItem.of(nameItem(new ItemStack(Material.TNT),
                "&4&lDELETE ARENA", "&7This cannot be undone."), e -> {
            List<Team> teams = new ArrayList<>(arena.getTeams());
            teams.remove(team);
            arena.setTeams(teams);
            ArenaManager.write(arena);
            ArenaManager.add(arena);
            menus.openTeamsMenu(player, arena);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
