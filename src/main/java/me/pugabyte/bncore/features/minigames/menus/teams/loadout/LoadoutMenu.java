package me.pugabyte.bncore.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LoadoutMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	Team team;
	MinigamesMenus menus = new MinigamesMenus();
	TeamMenus teamMenus = new TeamMenus();

	public LoadoutMenu(Arena arena, Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		//Back Item
		contents.set(0, 0, ClickableItem.of(backItem(), e -> teamMenus.openTeamsEditorMenu(player, arena, team)));
		//Copy Inventory Item
		contents.set(0, 1, ClickableItem.of(nameItem(new ItemStack(Material.ANVIL), "&eCopy Inventory", "&3This will copy all the||&3contents of your inventory||&3into the team's loadout."), e -> {
			team.getLoadout().setInventoryContents(e.getWhoClicked().getInventory().getContents());
			ArenaManager.write(arena);
			ArenaManager.add(arena);
			teamMenus.openLoadoutMenu(player, arena, team);
		}));
		//Potion Effects Item
		contents.set(0, 2, ClickableItem.of(nameItem(new ItemStack(Material.POTION), "Potion Effects"), e -> teamMenus.openPotionEffectsMenu(player, arena, team)));
		//Delete Loadout Item
		contents.set(0, 3, ClickableItem.of(nameItem(new ItemStack(Material.TNT),
				"&c&lDelete Loadout", "&7You will need to confirm||&7deleting a loadout.|| ||&7&lTHIS CANNOT BE UNDONE."), e -> teamMenus.openDeleteLoadoutMenu(player, arena, team)));

		//Armor Item
		contents.set(0, 4, ClickableItem.empty(nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorType.RED.getDurability().byteValue()), "&eArmor ➝")));
		//HotBar Items
		contents.fillRect(4, 2, 4, 8, ClickableItem.empty(nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorType.RED.getDurability().byteValue()), "&e⬇ Hot Bar ⬇")));
		//Offhand Item
		contents.set(4, 1, ClickableItem.empty(nameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorType.RED.getDurability().byteValue()), "&e← Offhand")));

		if (!(team.getLoadout().getInventoryContents().length > 0)) return;
		for (int i = 0; i < 9; i++) {
			if (team.getLoadout().getInventoryContents()[i] == null) continue;
			contents.set(5, i, ClickableItem.empty(team.getLoadout().getInventoryContents()[i]));
		}
		int row = 1;
		int column = 0;
		for (int i = 9; i < 36; i++) {
			if (team.getLoadout().getInventoryContents()[i] != null) {
				contents.set(row, column, ClickableItem.empty(team.getLoadout().getInventoryContents()[i]));
			}
			if (column != 8) {
				column++;
			} else {
				column = 0;
				row++;
			}
		}
		if (team.getLoadout().getInventoryContents()[40] != null) {
			contents.set(4, 0, ClickableItem.empty(team.getLoadout().getInventoryContents()[40]));
		}
		int spot = 8;
		for (int i = 36; i < 40; i++) {
			if (team.getLoadout().getInventoryContents()[i] != null) {
				contents.set(0, spot, ClickableItem.empty(team.getLoadout().getInventoryContents()[i]));
			}
			spot--;
		}


	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
