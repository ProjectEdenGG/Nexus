package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TeamColorMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

	public static final LinkedHashSet<ColorType> COLOR_TYPES = new LinkedHashSet<>(Arrays.stream(ColorType.values())
		.filter(colorType -> colorType != ColorType.BLACK).collect(Collectors.toList()));

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title("Team Color Menu")
			.rows(3)
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> new TeamEditorMenu(arena, team).open(player));

		int column = 0;
		int row = 1;
		for (ColorType colorType : COLOR_TYPES) {
			ItemBuilder item = new ItemBuilder(colorType.getWool())
				.name(colorType.getDisplayName())
				.glow(colorType.getChatColor() == team.getChatColor());

			contents.set(row, column, ClickableItem.of(item, e -> {
				team.setChatColor(colorType.getChatColor());
				arena.write();
				new TeamColorMenu(arena, team).open(player);

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
