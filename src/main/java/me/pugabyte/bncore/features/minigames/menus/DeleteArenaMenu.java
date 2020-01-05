package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
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
import java.util.stream.Stream;

public class DeleteArenaMenu extends MenuUtils implements InventoryProvider {

    Arena arena;
    MinigamesMenus menus = new MinigamesMenus();
    public DeleteArenaMenu(Arena arena){
        this.arena = arena;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRect(0, 0, 2, 8, ClickableItem.of(nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
                "&7Cancel"), e -> menus.openArenaMenu(player, arena)));
        contents.fillRect(1, 1, 1, 7, ClickableItem.of(nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1,(byte) 5),
                "&7Cancel"), e -> menus.openArenaMenu(player, arena)));
        contents.set(1, 4, ClickableItem.of(nameItem(new ItemStack(Material.TNT),
                "&4&lDELETE ARENA", "&7This cannot be undone."), e -> {
            try (Stream<Path> paths = Files.walk(Paths.get(ArenaManager.getFolder()))) {
                paths.forEach(filePath -> {
                    if (!Files.isRegularFile(filePath)) return;

                    String name = filePath.getFileName().toString().replace(".yml", "");
                    if (name.startsWith(".")) return;
                    FileConfiguration config = YamlConfiguration.loadConfiguration(filePath.toFile());
                    if(!config.get("arena").equals(arena)) return;
                    player.sendMessage("arena ==");
                    filePath.toFile().delete();
                    ArenaManager.remove(arena);
                    player.closeInventory();
                    player.sendMessage(Utils.colorize(Utils.getPrefix("JMinigames") + "Successfully deleted the arena " + arena.getName()));
                });
            } catch (IOException ex) {
                BNCore.severe("An error occurred while trying to read arena configuration files: " + ex.getMessage());
            }
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
