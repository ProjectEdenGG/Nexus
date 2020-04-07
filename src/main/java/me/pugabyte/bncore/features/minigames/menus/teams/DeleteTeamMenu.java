package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		ItemStack cancel = nameItem(Material.LIME_STAINED_GLASS_PANE, "&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.from(cancel, e -> menus.openTeamsMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.from(cancel, e -> menus.openTeamsMenu(player, arena)));

		contents.set(1, 4, ClickableItem.from(nameItem(Material.TNT, "&4&lDELETE ARENA", "&7This cannot be undone."), e -> {
			arena.getTeams().remove(team);
			arena.write();
			menus.openTeamsMenu(player, arena);
		}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
