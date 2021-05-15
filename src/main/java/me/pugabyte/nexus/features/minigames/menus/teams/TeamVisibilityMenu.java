package me.pugabyte.nexus.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.lucko.helper.scoreboard.ScoreboardTeam.NameTagVisibility;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class TeamVisibilityMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	TeamMenus teamMenus = new TeamMenus();

	public TeamVisibilityMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	private Material getIcon(NameTagVisibility nameTagVisibility) {
		return switch (nameTagVisibility) {
			case ALWAYS -> Material.BLACK_CONCRETE;
			case NEVER -> Material.GLASS;
			case HIDE_FOR_OWN_TEAM -> Material.REDSTONE_LAMP;
			case HIDE_FOR_OTHER_TEAMS -> Material.GLOWSTONE;
		};
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> teamMenus.openTeamsEditorMenu(player, arena, team));

		int column = 0;
		for (NameTagVisibility visibility : NameTagVisibility.values()) {
			ItemStack item = nameItem(getIcon(visibility), "&e"+camelCase(visibility));
			if (team.getNameTagVisibility() == visibility)
				addGlowing(item);
			contents.set(1, column, ClickableItem.from(item, e -> {
				team.setNameTagVisibility(visibility);
				arena.write();
				teamMenus.openTeamsVisibilityMenu(player, arena, team);
			}));
			column++;
		}
	}

}
