package gg.projecteden.nexus.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ColorType;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class TeamColorMenu extends MenuUtils implements InventoryProvider {
	public static final LinkedHashSet<ColorType> COLOR_TYPES = new LinkedHashSet<>(Arrays.stream(ColorType.values()).filter(
			colorType -> colorType != ColorType.BLACK)
			.collect(Collectors.toList()));

	Arena arena;
	Team team;
	TeamMenus teamMenus = new TeamMenus();

	public TeamColorMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> teamMenus.openTeamsEditorMenu(player, arena, team));

		int column = 0;
		int row = 1;
		for (ColorType colorType : COLOR_TYPES) {
			ItemStack item = nameItem(colorType.getWool(), colorType.getDisplayName());

			if (colorType.getChatColor() == team.getChatColor())
				addGlowing(item);

			contents.set(row, column, ClickableItem.from(item, e -> {
				team.setChatColor(colorType.getChatColor());
				arena.write();
				teamMenus.openTeamsColorMenu(player, arena, team);
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
