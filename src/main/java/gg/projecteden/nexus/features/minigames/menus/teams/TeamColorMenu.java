package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Rows(3)
@Title("Team Color Menu")
@RequiredArgsConstructor
public class TeamColorMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

	public static final Set<ColorType> COLOR_TYPES = new LinkedHashSet<>(Arrays.stream(ColorType.values())
		.filter(colorType -> colorType != ColorType.BLACK).collect(Collectors.toList()));

	@Override
	public void init() {
		addBackItem(e -> new TeamEditorMenu(arena, team).open(viewer));

		int column = 0;
		int row = 1;
		for (ColorType colorType : COLOR_TYPES) {
			ItemBuilder item = new ItemBuilder(colorType.getWool())
				.name(colorType.getDisplayName())
				.glow(colorType.getChatColor() == team.getChatColor());

			contents.set(row, column, ClickableItem.of(item, e -> {
				team.setChatColor(colorType.getChatColor());
				arena.write();
				new TeamColorMenu(arena, team).open(viewer);

			}));

			if (column != 8) {
				column++;
			} else {
				column = 1;
				row++;
			}
		}
	}

}
