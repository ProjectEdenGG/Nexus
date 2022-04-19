package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.menus.flags.BlockListMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;

@Getter
public class MinigamesMenus extends MenuUtils {

	public SmartInventory blockListMenu(Arena arena) {
		return SmartInventory.builder()
			.provider(new BlockListMenu(arena))
			.title("Block List Menu")
			.maxSize()
			.build();
	}

	@SneakyThrows
	public void openCustomSettingsMenu(Player player, Arena arena) {
		Class<? extends InventoryProvider> provider = null;

		customMenus:
		for (Class<? extends InventoryProvider> menu : new Reflections("gg.projecteden.nexus.features.minigames.menus.custom").getSubTypesOf(InventoryProvider.class)) {
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

		provider.getDeclaredConstructor(Arena.class).newInstance(arena).open(player);
	}

}
