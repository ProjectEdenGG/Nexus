package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Loadout;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;

public class DeleteLoadoutMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;

	public DeleteLoadoutMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemBuilder cancel = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.of(cancel, e -> menus.getTeamMenus().openTeamsMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.of(cancel, e -> menus.getTeamMenus().openTeamsMenu(player, arena)));

		contents.set(1, 4, ClickableItem.of(Material.TNT, "&4&lDELETE LOADOUT", "&7This cannot be undone.", e -> {
			team.setLoadout(new Loadout());
			arena.write();
			menus.getTeamMenus().openLoadoutMenu(player, arena, team);
		}));
	}

}
