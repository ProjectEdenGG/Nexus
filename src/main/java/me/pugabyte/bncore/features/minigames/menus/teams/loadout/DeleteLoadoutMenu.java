package me.pugabyte.bncore.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class DeleteLoadoutMenu extends MenuUtils implements InventoryProvider {

    Arena arena;
    Team team;
    TeamMenus menus = new TeamMenus();
    public DeleteLoadoutMenu(Arena arena, Team team){
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
                "&4&lDELETE LOADOUT", "&7This cannot be undone."), e -> {
            team.getLoadout().setInventoryContents(new ItemStack[]{});
            team.getLoadout().setPotionEffects(new ArrayList<PotionEffect>());
            ArenaManager.write(arena);
            ArenaManager.add(arena);
            menus.openTeamsEditorMenu(player, arena, team);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
