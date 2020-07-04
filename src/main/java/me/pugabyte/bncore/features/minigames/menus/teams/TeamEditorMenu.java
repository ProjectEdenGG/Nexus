package me.pugabyte.bncore.features.minigames.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class TeamEditorMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	TeamMenus teamMenus = new TeamMenus();

	public TeamEditorMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	static void openAnvilMenu(Player player, Arena arena, Team team, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> menus.getTeamMenus().openTeamsEditorMenu(player, arena, team)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> teamMenus.openTeamsMenu(player, arena));

		contents.set(0, 8, ClickableItem.from(nameItem(
				Material.TNT,
				"&c&lDelete Team",
				"&7You will need to confirm||&7deleting a team.|| ||&7&lTHIS CANNOT BE UNDONE."
			),
			e -> teamMenus.openDeleteTeamMenu(player, arena, team)));

		contents.set(1, 0, ClickableItem.from(nameItem(
				Material.BOOK,
				"&eTeam Name",
				"||&3Current Name:||&e" + team.getName()
			),
			e -> openAnvilMenu(player, arena, team, (team.getName() == null) ? "Default" : team.getName(), (p, text) -> {
				team.setName(text);
				arena.write();
				teamMenus.openTeamsEditorMenu(player, arena, team);
				return AnvilGUI.Response.text(text);
			})));

		contents.set(1, 2, ClickableItem.from(nameItem(
				Material.OAK_SIGN,
				"&eTeam Objective",
				"||&3Current Objective:||&e" + team.getObjective()
			),
			e -> openAnvilMenu(player, arena, team, (team.getObjective() == null) ? "Objective" : team.getObjective(), (p, text) -> {
				team.setObjective(text);
				arena.write();
				teamMenus.openTeamsEditorMenu(player, arena, team);
				return AnvilGUI.Response.text(text);
			})));

		contents.set(1, 4, ClickableItem.from(nameItem(
				ColorType.of(team.getColor()).getWool(),
				"&eTeam Color",
				"&7Set the color of the team"
			),
			e -> teamMenus.openTeamsColorMenu(player, arena, team)));

		contents.set(1, 6, ClickableItem.from(nameItem(
				Material.COMPASS,
				"&eSpawnpoint Locations",
				"&7Set locations the players||&7on the team can spawn."
			),
			e -> teamMenus.openSpawnpointMenu(arena, team).open(player)));

		contents.set(1, 8, ClickableItem.from(nameItem(
				Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
				"&eBalance Percentage",
				"&7Set to -1 to disable||&7team balancing.|| ||&3Current Percentage:||&e" + team.getBalancePercentage()
			),
			e -> openAnvilMenu(player, arena, team, String.valueOf(team.getBalancePercentage()), (p, text) -> {
				if (Utils.isInt(text)) {
					team.setBalancePercentage(Integer.parseInt(text));
					arena.write();
					teamMenus.openTeamsEditorMenu(player, arena, team);
					return AnvilGUI.Response.text(text);
				} else {
					player.sendMessage(Minigames.PREFIX + "The balance percentage must be an integer.");
					return AnvilGUI.Response.close();
				}
			})));

		contents.set(2, 4, ClickableItem.from(nameItem(
					Material.CHEST,
					"&eLoadout"
				),
				e -> teamMenus.openLoadoutMenu(player, arena, team)));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
