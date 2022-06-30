package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.menus.custom.ICustomMechanicMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;

@Title("Game Mechanic Type")
@RequiredArgsConstructor
public class MechanicsMenu extends InventoryProvider {
	private final Arena arena;

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(MechanicType.values().length, 1);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(player));
		int row = 1;
		int column = 0;
		for (MechanicType mechanic : MechanicType.values()) {
			ItemStack menuItem = mechanic.get().getMenuItem();

			ItemBuilder item = new ItemBuilder(menuItem.clone())
				.name("&e" + mechanic.get().getName())
				.glow(arena.getMechanicType() == mechanic);

			contents.set(row, column, ClickableItem.of(item, e -> {
				arena.setMechanicType(mechanic);
				arena.write();
				new MechanicsMenu(arena).open(player);

			}));

			if (column != 8) {
				column++;
			} else {
				column = 0;
				row++;
			}
		}

	}

	@SneakyThrows
	public static void openCustomSettingsMenu(Player player, Arena arena) {
		Class<? extends InventoryProvider> provider = null;

		customMenus:
		for (Class<? extends InventoryProvider> menu : new Reflections(ICustomMechanicMenu.class.getPackageName()).getSubTypesOf(InventoryProvider.class)) {
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

		final InventoryProvider menu = provider.getDeclaredConstructor(Arena.class).newInstance(arena);
		arena.write();
		menu.open(player);
	}

}
