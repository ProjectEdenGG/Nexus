package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeamEditorMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	Team team;
	MinigamesMenus menus = new MinigamesMenus();
	TeamMenus teamMenus = new TeamMenus();

	public TeamEditorMenu(Arena arena, Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		//Back Item
		contents.set(0, 0, ClickableItem.of(backItem(), e -> teamMenus.openTeamsMenu(player, arena)));
		//Delete Team Item
		contents.set(0, 8, ClickableItem.of(nameItem(new ItemStack(Material.TNT),
				"&c&lDelete Team", "&7You will need to confirm||&7deleting a team.|| ||&7&lTHIS CANNOT BE UNDONE."), e -> {
			player.closeInventory();
			teamMenus.openDeleteTeamMenu(player, arena, team);
		}));

		//Name Item
		contents.set(1, 0, ClickableItem.of(nameItem(new ItemStack(Material.BOOK),
				"&eTeam Name", " ||&3Current Name:||&e" + team.getName()), e -> {
			player.closeInventory();
			new AnvilGUI.Builder()
					.onClose(p -> teamMenus.openTeamsEditorMenu(player, arena, team))
					.onComplete((p, text) -> {
						team.setName(text);
						ArenaManager.write(arena);
						teamMenus.openTeamsEditorMenu(player, arena, team);
						return AnvilGUI.Response.text(text);
					})
					.text("Team Name")
					.plugin(BNCore.getInstance())
					.open(player);
		}));
		//Objective Item
		contents.set(1, 2, ClickableItem.of(nameItem(new ItemStack(Material.SIGN),
				"&eTeam Objective", "||&3Current Objective:||&e" + team.getObjective()), e -> {
			new AnvilGUI.Builder()
					.onClose(p -> teamMenus.openTeamsEditorMenu(player, arena, team))
					.onComplete((p, text) -> {
						team.setObjective(text);
						ArenaManager.write(arena);
						teamMenus.openTeamsEditorMenu(player, arena, team);
						return AnvilGUI.Response.text(text);
					})
					.text("Team Objective")
					.plugin(BNCore.getInstance())
					.open(player);
		}));
		//Team Color Item
		contents.set(1, 4, ClickableItem.of(nameItem(new ItemStack(Material.WOOL, 1, ColorType.fromChatColor(team.getColor()).getDurability().byteValue()),
				"&eTeam Color", "&7Set the color of the team"), e -> teamMenus.openTeamsColorMenu(player, arena, team)));
		//Spawnpoints Item
		contents.set(1, 6, ClickableItem.of(nameItem(new ItemStack(Material.COMPASS),
				"&eSpawnpoint Locations", "&7Set locations the players||&7on the team can spawn."), e -> {
			teamMenus.openSpawnpointMenu(arena, team).open(player);
		}));
		//Balance Percentage Item7
		contents.set(1, 8, ClickableItem.of(nameItem(new ItemStack(Material.IRON_PLATE),
				"&eBalance Percentage", "&7Set to -1 to disable||&7team balancing.|| ||&3Current Percentage:||&e" + team.getBalancePercentage()), e -> {
			player.closeInventory();
			new AnvilGUI.Builder()
					.onClose(p -> teamMenus.openTeamsEditorMenu(player, arena, team))
					.onComplete((p, text) -> {
						if (!Utils.isInt(text)) {
							player.closeInventory();
							player.sendMessage(Utils.getPrefix("JMinigames") + "The balance percentage must be an integer.");
							return AnvilGUI.Response.close();
						}
						team.setBalancePercentage(Integer.parseInt(text));
						ArenaManager.write(arena);
						teamMenus.openTeamsEditorMenu(player, arena, team);
						return AnvilGUI.Response.text(text);
					})
					.text("Team Name")
					.plugin(BNCore.getInstance())
					.open(player);
		}));
		//Loadout Item
		contents.set(2, 4, ClickableItem.of(nameItem(new ItemStack(Material.CHEST),
				"Loadout"), e -> teamMenus.openLoadoutMenu(player, arena, team)));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
