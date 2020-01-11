package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Loadout;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ColorType;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class TeamsMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	MinigamesMenus menus = new MinigamesMenus();
	TeamMenus teamMenus = new TeamMenus();

	public TeamsMenu(Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> menus.openArenaMenu(player, arena)));
		contents.set(0, 4, ClickableItem.of(nameItem(new ItemStack(Material.EMERALD_BLOCK), "&aAdd Team"), e -> {
			new AnvilGUI.Builder()
					.onClose(p -> {
						teamMenus.openTeamsMenu(player, arena);
					})
					.onComplete((p, text) -> {
						List<Team> teams = new ArrayList<>();
						teams.addAll(arena.getTeams());
						teams.add(Team.builder()
								.name(text)
								.color(ChatColor.WHITE)
								.objective("Win")
								.loadout(Loadout.builder()
										.inventoryContents(new ItemStack[]{})
										.potionEffects(new ArrayList<PotionEffect>())
										.build())
								.spawnpoints(new ArrayList<Location>())
								.build());
						arena.setTeams(teams);
						ArenaManager.write(arena);
						ArenaManager.add(arena);
						teamMenus.openTeamsMenu(player, arena);
						return AnvilGUI.Response.text(text);
					})
					.text("Team Name")
					.plugin(BNCore.getInstance())
					.open(player);
		}));
		int row = 1;
		int column = 0;
		for (Team team : arena.getTeams()) {
			ItemStack item = nameItem(new ItemStack(Material.WOOL, 1, ColorType.fromChatColor(team.getColor()).getDurability().byteValue()), "&e" + team.getColoredName());
			contents.set(row, column, ClickableItem.of(item, e -> {
				teamMenus.openTeamsEditorMenu(player, arena, team);
			}));
			if (column != 8) {
				column++;
			} else {
				column = 1;
				row++;
			}
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}