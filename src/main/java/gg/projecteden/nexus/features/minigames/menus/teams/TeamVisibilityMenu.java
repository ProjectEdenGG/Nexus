package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.scoreboard.NameTagVisibility;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Rows(2)
@Title("Team Visibility Menu")
@RequiredArgsConstructor
public class TeamVisibilityMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

	private Material getIcon(NameTagVisibility nameTagVisibility) {
		return switch (nameTagVisibility) {
			case ALWAYS -> Material.BLACK_CONCRETE;
			case NEVER -> Material.GLASS;
			case HIDE_FOR_OWN_TEAM -> Material.REDSTONE_LAMP;
			case HIDE_FOR_OTHER_TEAMS -> Material.GLOWSTONE;
		};
	}

	@Override
	public void init() {
		addBackItem(e -> new TeamEditorMenu(arena, team).open(viewer));

		int column = 0;
		for (NameTagVisibility visibility : NameTagVisibility.values()) {
			ItemBuilder item = new ItemBuilder(getIcon(visibility))
				.name("&e" + camelCase(visibility))
				.glow(team.getNameTagVisibility() == visibility);
			contents.set(1, column, ClickableItem.of(item, e -> {
				team.setNameTagVisibility(visibility);
				arena.write();
				new TeamVisibilityMenu(arena, team).open(viewer);

			}));
			column++;
		}
	}

}
