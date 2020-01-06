package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.util.Set;

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

    @SneakyThrows
    public void openCustomSettingsMenu(Player player, Arena arena){
        Set<Class<?>> classes = new Reflections("me.pugabyte.bncore.features.minigames.menus.custommenus").getTypesAnnotatedWith(CustomMechanicSettings.class);
        Class provider = null;
        for (Class<?> clazz : classes){
            if (clazz.getAnnotation(CustomMechanicSettings.class).value().equals(arena.getMechanicType())){
                provider = clazz;
                break;
            }
        }
        if(provider == null) {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            return;
        }
        SmartInventory INV = SmartInventory.builder()
                .id("customSettingsMenu")
                .provider((InventoryProvider) provider.getDeclaredConstructor(Arena.class).newInstance(arena))
                .title("Custom Settings Menu")
                .size(6,9)
                .build();
        INV.open(player);
    }
}
