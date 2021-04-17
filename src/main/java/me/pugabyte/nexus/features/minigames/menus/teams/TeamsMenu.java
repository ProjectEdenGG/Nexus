package me.pugabyte.nexus.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.Tasks;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

import static me.pugabyte.nexus.features.minigames.Minigames.menus;

public class TeamsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public TeamsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> menus.getTeamMenus().openTeamsMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> menus.openArenaMenu(player, arena));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.EMERALD_BLOCK, "&aAdd Team"),
			e -> openAnvilMenu(player, arena, "Default", (p, text) -> {
				arena.getTeams().add(new Team(text));
				arena.write();
				menus.getTeamMenus().openTeamsMenu(player, arena);
				return AnvilGUI.Response.text(text);
			})));

		int row = 1;
		int column = 0;
		for (Team team : arena.getTeams()) {
			ItemStack item = new ItemStack(ColorType.of(team.getChatColor()).getWool());
			contents.set(row, column, ClickableItem.from(nameItem(item, "&e" + team.getColoredName()),
					e -> menus.getTeamMenus().openTeamsEditorMenu(player, arena, team)));

			if (column != 8) {
				column++;
			} else {
				column = 1;
				row++;
			}
		}
	}

}