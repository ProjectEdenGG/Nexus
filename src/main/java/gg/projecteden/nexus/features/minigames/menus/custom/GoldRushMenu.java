package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.GoldRush;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.MechanicsMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.GoldRushArena;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

@CustomMechanicSettings(GoldRush.class)
public class GoldRushMenu extends ICustomMechanicMenu {
	private final GoldRushArena arena;

	public GoldRushMenu(Arena arena){
		this.arena = ArenaManager.convert(arena, GoldRushArena.class);
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> MechanicsMenu.openCustomSettingsMenu(player, arena)));
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		String currentValue = (arena.getMineStackHeight() > 0) ? "" + arena.getMineStackHeight() : "null";

		contents.set(1, 4, ClickableItem.of(new ItemBuilder(Material.LADDER).name("&eMine Stack Height").lore("&eCurrent value:", ""), e -> {
			openAnvilMenu(viewer, arena, currentValue, (Player p, String text) -> {
				if (!Utils.isInt(text)) {
					AnvilGUI.Response.close();
					throw new InvalidInputException(Minigames.PREFIX + "You must use an integer for Mine Stack Height.");
				}
				arena.setMineStackHeight(Integer.parseInt(text));
				ArenaManager.write(arena);
				MechanicsMenu.openCustomSettingsMenu(viewer, arena);
				return AnvilGUI.Response.text(text);
			});
		}));
	}
}
