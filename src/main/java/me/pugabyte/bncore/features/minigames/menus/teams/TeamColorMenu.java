package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ColorType;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamColorMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	Team team;
	MinigamesMenus menus = new MinigamesMenus();
	TeamMenus teamMenus = new TeamMenus();

	public TeamColorMenu(Arena arena, Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		//Back Item
		contents.set(0, 0, ClickableItem.of(backItem(), e -> {
			teamMenus.openTeamsEditorMenu(player, arena, team);
		}));

		//Wool Items
		ArrayList<ItemStack> items = new ArrayList<ItemStack>() {{
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.RED.getWoolData()), "&cRed"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getWoolData()), "&6Orange"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getWoolData()), "&eYellow"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.LIME.getWoolData()), "&aLime"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getWoolData()), "&2Green"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.CYAN.getWoolData()), "&3Cyan"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getWoolData()), "&bLight Blue"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getWoolData()), "&9Blue"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getWoolData()), "&5Purple"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.PINK.getWoolData()), "&dPink"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.WHITE.getWoolData()), "&rWhite"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.SILVER.getWoolData()), "&7Light Gray"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.GRAY.getWoolData()), "&8Gray"));
			add(nameItem(new ItemStack(Material.WOOL, 1, DyeColor.BLACK.getWoolData()), "&0Black"));
		}};
		int column = 0;
		int row = 1;
		for (ItemStack item : items) {
			if (ColorType.fromString(ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase())).getChatColor() == team.getColor()) {
				item = itemGlow(item);
			}
			contents.set(row, column, ClickableItem.of(item.clone(), e -> {
				List<Team> teams = new ArrayList<>(arena.getTeams());
				teams.remove(team);
				team.setColor(ColorType.fromString(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase())).getChatColor());
				teams.add(team);
				arena.setTeams(teams);
				ArenaManager.write(arena);
				ArenaManager.add(arena);
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
