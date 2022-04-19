package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeleteTeamMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	TeamMenus menus = new TeamMenus();

	public DeleteTeamMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemBuilder cancel = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.of(cancel, e -> menus.openTeamsMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.of(cancel, e -> menus.openTeamsMenu(player, arena)));

		contents.set(1, 4, ClickableItem.of(Material.TNT, "&4&lDELETE ARENA", "&7This cannot be undone.", e -> {
			arena.getTeams().remove(team);
			arena.write();
			menus.openTeamsMenu(player, arena);
		}));
	}

}
