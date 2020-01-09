package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MechanicsMenu extends MenuUtils implements InventoryProvider {

    MinigamesMenus menus = new MinigamesMenus();
    Arena arena;
    public MechanicsMenu(Arena arena){
        this.arena = arena;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(0, 0, ClickableItem.of(backItem(), e -> menus.openArenaMenu(player, arena)));
        int row = 1;
        int column = 0;
        for(MechanicType mechanic : MechanicType.values()){
            ItemStack item = nameItem(mechanic.getItemStack().clone(), "&e" + (ChatColor.stripColor(mechanic.name().replace("_", " "))));
            if(arena.getMechanicType() == mechanic){
                item = itemGlow(item);
            }
            contents.set(row, column, ClickableItem.of(item, e -> {
                arena.setMechanicType(mechanic);
                ArenaManager.write(arena);
                ArenaManager.add(arena);
                menus.openMechanicsMenu(player, arena);
            }));
            if(column != 8){
                column++;
            } else {
                column = 0;
                row++;
            }
        }

    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {
    }
}
