package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Loadout;
import gg.projecteden.nexus.features.minigames.models.Team;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		ItemStack cancel = nameItem(Material.LIME_STAINED_GLASS_PANE, "&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.from(cancel, e -> menus.getTeamMenus().openTeamsMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.from(cancel, e -> menus.getTeamMenus().openTeamsMenu(player, arena)));

		contents.set(1, 4, ClickableItem.from(nameItem(Material.TNT, "&4&lDELETE LOADOUT", "&7This cannot be undone."), e -> {
			team.setLoadout(new Loadout());
			arena.write();
			menus.getTeamMenus().openLoadoutMenu(player, arena, team);
		}));
	}

}
