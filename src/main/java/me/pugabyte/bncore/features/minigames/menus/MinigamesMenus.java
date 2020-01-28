package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.menus.flags.BlockListMenu;
import me.pugabyte.bncore.features.minigames.menus.flags.FlagsMenu;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;

public class MinigamesMenus extends MenuUtils {
	@Getter
	private TeamMenus teamMenus = new TeamMenus();

	public void openArenaMenu(Player player, Arena arena) {
		SmartInventory inv = SmartInventory.builder()
				.id("minigameManager")
				.title(arena.getDisplayName())
				.provider(new ArenaMenu(arena))
				.size(5, 9)
				.build();
		inv.open(player);
	}

	public void openDeleteMenu(Player player, Arena arena) {
		SmartInventory INV = SmartInventory.builder()
				.id("deleteArenaMenu")
				.title("Delete Arena?")
				.provider(new DeleteArenaMenu(arena))
				.size(3, 9)
				.build();
		INV.open(player);
	}

	public void openMechanicsMenu(Player player, Arena arena) {
		SmartInventory INV = SmartInventory.builder()
				.id("mechanicMenu")
				.title("Game Mechanic Type")
				.size(1 + getRows(MechanicType.values().length), 9)
				.provider(new MechanicsMenu(arena))
				.build();
		INV.open(player);
	}

	public void openLobbyMenu(Player player, Arena arena) {
		SmartInventory INV = SmartInventory.builder()
				.id("lobbyMenu")
				.title("Lobby Menu")
				.provider(new LobbyMenu(arena))
				.size(2, 9)
				.build();
		INV.open(player);
	}

	public void openFlagsMenu(Player player, Arena arena) {
		SmartInventory INV = SmartInventory.builder()
				.id("flagsMenu")
				.title("Flags Menu")
				.provider(new FlagsMenu(arena))
				.size(3, 9)
				.build();
		INV.open(player);
	}

	public SmartInventory blockListMenu(Arena arena) {
		SmartInventory INV = SmartInventory.builder()
				.id("blockListMenu")
				.title("Block List Menu")
				.provider(new BlockListMenu(arena))
				.size(6, 9)
				.build();
		return INV;
	}

	@SneakyThrows
	public void openCustomSettingsMenu(Player player, Arena arena) {
		Class<? extends InventoryProvider> provider = null;

		customMenus:
		for (Class<? extends InventoryProvider> menu : new Reflections("me.pugabyte.bncore.features.minigames.menus.custom").getSubTypesOf(InventoryProvider.class)) {
			for (Class<? extends Mechanic> superclass : arena.getMechanic().getSuperclasses()) {
				if (menu.getAnnotation(CustomMechanicSettings.class) != null) {
					List<Class<? extends Mechanic>> classes = Arrays.asList(menu.getAnnotation(CustomMechanicSettings.class).value());
					if (classes.contains(superclass)) {
						provider = menu;
						break customMenus;
					}
				}
			}
		}

		if (provider == null) {
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
			return;
		}

		SmartInventory INV = SmartInventory.builder()
				.id("customSettingsMenu")
				.provider(provider.getDeclaredConstructor(Arena.class).newInstance(arena))
				.title("Custom Settings Menu")
				.size(3, 9)
				.build();
		INV.open(player);
	}

}
