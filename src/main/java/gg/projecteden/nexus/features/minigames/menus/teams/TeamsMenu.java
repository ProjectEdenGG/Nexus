package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

@Rows(2)
@Title("Teams Menu")
@RequiredArgsConstructor
public class TeamsMenu extends InventoryProvider {
	private final Arena arena;

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> new TeamsMenu(arena).open(player)));
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		contents.set(0, 4, ClickableItem.of(Material.EMERALD_BLOCK, "&aAdd Team",
			e -> openAnvilMenu(viewer, arena, "Default", (p, text) -> {
				arena.getTeams().add(new Team(text));
				arena.write();
				new TeamsMenu(arena).open(viewer);

				return AnvilGUI.Response.text(text);
			})));

		int row = 1;
		int column = 0;
		for (Team team : arena.getTeams()) {
			ItemStack item = new ItemStack(ColorType.of(team.getChatColor()).getWool());
			contents.set(row, column, ClickableItem.of(item, "&e" + team.getColoredName(),
				e -> new TeamEditorMenu(arena, team).open(viewer)));

			if (column != 8) {
				column++;
			} else {
				column = 1;
				row++;
			}
		}
	}

}
