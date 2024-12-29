package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.teams.TeamEditorMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Loadout;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Title("Loadout Menu")
@RequiredArgsConstructor
public class LoadoutMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

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
	public void init() {
		contents.set(0, 0, ClickableItem.of(new ItemBuilder(backItem())
				.name(backItem().getItemMeta().getDisplayName())
				.lore("&7Escape to discard changes"),
			e -> {
				save(viewer);
				new TeamEditorMenu(arena, team).open(viewer);

			}));

		contents.set(0, 1, ClickableItem.of(new ItemBuilder(Material.ANVIL)
				.name("&eCopy From Inventory")
				.lore("&3This will copy all the", "&3contents of your inventory", "&3into the team's loadout."),
			e -> {
				team.getLoadout().setInventory(viewer.getInventory().getContents().clone());
				arena.write();
				new LoadoutMenu(arena, team).open(viewer);

			}));

		contents.set(0, 2, ClickableItem.of(new ItemBuilder(Material.ANVIL)
				.name("&eCopy To Inventory")
				.lore("&3This will copy all the", "&3contents of the loadout", "&3into your inventory."),
			e -> {
				viewer.getInventory().setContents(team.getLoadout().getInventory().clone());
				arena.write();
				new LoadoutMenu(arena, team).open(viewer);

			}));

		contents.set(0, 4, ClickableItem.of(
			new ItemBuilder(Material.POTION)
				.name("&ePotion Effects")
				.itemFlags(ItemFlag.HIDE_POTION_EFFECTS)
				.build(),
			e -> new PotionEffectsMenu(arena, team).open(viewer)));

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT)
				.name("&c&lDelete Loadout")
				.lore("&7You will need to confirm", "&7deleting a loadout.", "", "&7&lTHIS CANNOT BE UNDONE."),
			e -> ConfirmationMenu.builder()
				.onCancel(e2 -> open(viewer))
				.onConfirm(e2 -> {
					team.setLoadout(new Loadout());
					arena.write();
					new LoadoutMenu(arena, team).open(viewer);
				})
				.open(viewer)));

		MenuUtils.formatInventoryContents(contents, team.getLoadout().getInventory());
	}

}
