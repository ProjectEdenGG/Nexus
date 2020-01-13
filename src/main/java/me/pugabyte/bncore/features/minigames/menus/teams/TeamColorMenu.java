package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TeamColorMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	TeamMenus teamMenus = new TeamMenus();

	public TeamColorMenu(Arena arena, Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> teamMenus.openTeamsEditorMenu(player, arena, team));

		ArrayList<ItemStack> items = new ArrayList<>();

		for (ColorType colorType : ColorType.values())
			if (colorType.getChatColor() != null)
				items.add(nameItem(colorType.getItemStack(Material.WOOL), colorType.getDisplayName()));

		int column = 0;
		int row = 1;
		for (ItemStack item : items) {
			ColorType colorType = ColorType.fromDurability(item.getDurability());
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

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}
