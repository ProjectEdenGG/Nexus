package me.pugabyte.nexus.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.features.minigames.Minigames.menus;

public class LoadoutMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;

	public LoadoutMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	private void save(Player player) {
		Inventory inventory = player.getOpenInventory().getTopInventory();
		team.getLoadout().setInventory(new ItemStack[41]);

		// Hotbar

		for (int i = 0; i < 9; i++) {
			ItemStack item = inventory.getItem(i + 45);
			if (item != null)
				team.getLoadout().getInventory()[i] = item.clone();
		}

		// Inventory
		for (int i = 9; i < 36; i++) {
			ItemStack item = inventory.getItem(i);
			if (item != null)
				team.getLoadout().getInventory()[i] = item.clone();
		}

		// Offhand
		{
			ItemStack item = inventory.getItem(36);
			if (item != null)
				team.getLoadout().getInventory()[40] = item.clone();
		}

		// Armor
		int column = 8;
		for (int i = 36; i < 40; i++) {
			ItemStack item = inventory.getItem(36 + column);
			if (item != null)
				team.getLoadout().getInventory()[i] = item.clone();
			--column;
		}

		arena.write();

	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(nameItem(
				backItem(),
				backItem().getItemMeta().getDisplayName(),
				"&7Escape to discard changes"
			),
			e -> {
				save(player);
				menus.getTeamMenus().openTeamsEditorMenu(player, arena, team);
			}));

		contents.set(0, 1, ClickableItem.from(nameItem(
				Material.ANVIL,
				"&eCopy From Inventory",
				"&3This will copy all the||&3contents of your inventory||&3into the team's loadout."
			), e -> {
				team.getLoadout().setInventory(player.getInventory().getContents().clone());
				arena.write();
				menus.getTeamMenus().openLoadoutMenu(player, arena, team);
			}));

		contents.set(0, 2, ClickableItem.from(nameItem(
				Material.ANVIL,
				"&eCopy To Inventory",
				"&3This will copy all the||&3contents of the loadout||&3into your inventory."
			), e -> {
				player.getInventory().setContents(team.getLoadout().getInventory().clone());
				arena.write();
				menus.getTeamMenus().openLoadoutMenu(player, arena, team);
			}));

		contents.set(0, 4, ClickableItem.from(
			new ItemBuilder(Material.POTION)
					.name("&ePotion Effects")
					.itemFlags(ItemFlag.HIDE_POTION_EFFECTS)
					.build(),
			e -> menus.getTeamMenus().openPotionEffectsMenu(player, arena, team)));

		contents.set(0, 8, ClickableItem.from(nameItem(
				Material.TNT,
				"&c&lDelete Loadout",
				"&7You will need to confirm||&7deleting a loadout.|| ||&7&lTHIS CANNOT BE UNDONE."
			),
			e -> menus.getTeamMenus().openDeleteLoadoutMenu(player, arena, team)));

		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		contents.set(4, 4, ClickableItem.empty(nameItem(redPane.clone(), "&eArmor ➝")));
		contents.set(4, 1, ClickableItem.empty(nameItem(redPane.clone(), "&e← Offhand")));
		contents.fillRect(4, 2, 4, 3, ClickableItem.empty(nameItem(redPane.clone(), "&e⬇ Hot Bar ⬇")));

		if (team.getLoadout().getInventory() == null || team.getLoadout().getInventory().length == 0) return;

		// Hotbar
		for (int i = 0; i < 9; i++) {
			contents.setEditable(SlotPos.of(5, i), true);
			if (team.getLoadout().getInventory()[i] == null) continue;
			contents.set(5, i, ClickableItem.empty(team.getLoadout().getInventory()[i]));
		}

		// Inventory
		int row = 1;
		int column = 0;
		for (int i = 9; i < 36; i++) {
			contents.setEditable(SlotPos.of(row, column), true);
			if (team.getLoadout().getInventory()[i] != null)
				contents.set(row, column, ClickableItem.empty(team.getLoadout().getInventory()[i]));

			if (column != 8) {
				++column;
			} else {
				column = 0;
				++row;
			}
		}

		// Offhand
		contents.setEditable(SlotPos.of(4, 0), true);
		if (team.getLoadout().getInventory()[40] != null)
			contents.set(4, 0, ClickableItem.empty(team.getLoadout().getInventory()[40]));

		// Armor
		column = 8;
		for (int i = 36; i < 40; i++) {
			contents.setEditable(SlotPos.of(4, column), true);
			if (team.getLoadout().getInventory()[i] != null) {
				contents.set(4, column, ClickableItem.empty(team.getLoadout().getInventory()[i]));

			}
			--column;
		}


	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}
