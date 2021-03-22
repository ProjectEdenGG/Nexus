package me.pugabyte.nexus.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.utils.ColorType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeamColorMenu extends MenuUtils implements InventoryProvider {
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
		for (ColorType colorType : ColorType.values())
			if (colorType.getChatColor() != null && colorType.getDurability() != null && colorType.getChatColor() != ChatColor.BLACK) {
				ItemStack item = nameItem(colorType.getWool(), colorType.getDisplayName());

				if (colorType.getChatColor() == team.getColor())
					addGlowing(item);

				contents.set(row, column, ClickableItem.from(item, e -> {
					team.setColor(colorType.getChatColor());
					arena.write();
					teamMenus.openTeamsColorMenu(player, arena, team);
				}));

				if (column != 8) {
					column++;
				} else {
					column = 2;
					row++;
				}
			}
	}

}
