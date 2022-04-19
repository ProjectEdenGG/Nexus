package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;

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

		formatInventoryContents(contents, team.getLoadout().getInventory());
	}

}
