package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.menus.teams.loadout.LoadoutMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

@Rows(3)
@Title("Team Editor Menu")
@RequiredArgsConstructor
public class TeamEditorMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

	static void openAnvilMenu(Player player, Arena arena, Team team, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> new TeamEditorMenu(arena, team).open(player)));
	}

	@Override
	public void init() {
		addBackItem(e -> new TeamsMenu(arena).open(viewer));

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT)
				.name("&c&lDelete Team")
				.lore("&7You will need to confirm", "&7deleting a team.", "", "&7&lTHIS CANNOT BE UNDONE."),
			e -> ConfirmationMenu.builder()
				.onCancel(e2 -> open(viewer))
				.onConfirm(e2 -> {
					arena.getTeams().remove(team);
					arena.write();
					new TeamsMenu(arena).open(viewer);
				})
				.open(viewer)));

		contents.set(1, 0, ClickableItem.of(new ItemBuilder(Material.BOOK)
				.name("&eTeam Name")
				.lore("", "&3Current Name:", "&e" + team.getName()),
			e -> openAnvilMenu(viewer, arena, team, (Nullables.isNullOrEmpty(team.getName())) ? "Default" : team.getName(), (p, text) -> {
				team.setName(text);
				arena.write();
				new TeamEditorMenu(arena, team).open(viewer);

				return AnvilGUI.Response.text(text);
			})));

		contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.OAK_SIGN)
				.name("&eTeam Objective")
				.lore("", "&3Current Objective:", "&e" + team.getObjective()),
			e -> openAnvilMenu(viewer, arena, team, (team.getObjective() == null) ? "Objective" : team.getObjective(), (p, text) -> {
				team.setObjective(text);
				arena.write();
				new TeamEditorMenu(arena, team).open(viewer);

				return AnvilGUI.Response.text(text);
			})));

		contents.set(1, 4, ClickableItem.of(new ItemBuilder(ColorType.of(team.getChatColor()).getWool())
				.name("&eTeam Color")
				.lore("&7Set the color of the team"),
			e -> new TeamColorMenu(arena, team).open(viewer)));

		contents.set(1, 6, ClickableItem.of(new ItemBuilder(Material.COMPASS)
				.name("&eSpawnpoint Locations")
				.lore("&7Set locations the players", "&7on the team can spawn."),
			e -> new SpawnpointLocationsMenu(arena, team, this).open(viewer)));

		contents.set(1, 8, ClickableItem.of(new ItemBuilder(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
				.name("&eBalance Percentage")
				.lore("&7Set to -1 to disable", "&7team balancing.", "", "&3Current Percentage:", "&e" + team.getBalancePercentage()),
			e -> openAnvilMenu(viewer, arena, team, String.valueOf(team.getBalancePercentage()), (p, text) -> {
				if (Utils.isInt(text)) {
					team.setBalancePercentage(MathUtils.clamp(Integer.parseInt(text), -1, 100));
					arena.write();
					new TeamEditorMenu(arena, team).open(viewer);

					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(viewer, Minigames.PREFIX + "The balance percentage must be an integer.");
					return AnvilGUI.Response.close();
				}
			})));

		contents.set(2, 0, ClickableItem.of(new ItemBuilder(Material.RED_TULIP)
				.name("&eLives")
				.lore("&7Set to 0 to disable", "&7lives.", "", "&3Current Value:", "&e" + team.getLives()),
			e -> openAnvilMenu(viewer, arena, team, String.valueOf(team.getLives()), (p, text) -> {
				if (Utils.isInt(text)) {
					team.setLives(Math.max(Integer.parseInt(text), 0));
					arena.write();
					new TeamEditorMenu(arena, team).open(viewer);

					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(viewer, Minigames.PREFIX + "The lives value must be an integer.");
					return AnvilGUI.Response.close();
				}
			})));

		contents.set(2, 2, ClickableItem.of(new ItemBuilder(Material.SKELETON_SKULL)
				.name("&eMinimum Players")
				.lore("&7Set to 0 to disable", "&7minimum players.", "", "&3Current Value:", "&e" + team.getMinPlayers()),
			e -> openAnvilMenu(viewer, arena, team, String.valueOf(team.getMinPlayers()), (p, text) -> {
				if (Utils.isInt(text)) {
					team.setMinPlayers(Math.max(Integer.parseInt(text), 0));
					arena.write();
					new TeamEditorMenu(arena, team).open(viewer);

					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(viewer, Minigames.PREFIX + "The minimum players value must be an integer.");
					return AnvilGUI.Response.close();
				}
			})));

		contents.set(2, 4, ClickableItem.of(Material.CHEST, "&eLoadout", e -> new LoadoutMenu(arena, team).open(viewer)));

		contents.set(2, 6, ClickableItem.of(new ItemBuilder(Material.PLAYER_HEAD)
				.name("&eMaximum Players")
				.lore("&7Set to -1 to disable", "&7maximum players.", "", "&3Current Value:", "&e" + team.getMaxPlayers()),
			e -> openAnvilMenu(viewer, arena, team, String.valueOf(team.getMaxPlayers()), (p, text) -> {
				if (Utils.isInt(text)) {
					team.setMaxPlayers(Math.max(Integer.parseInt(text), -1));
					arena.write();
					new TeamEditorMenu(arena, team).open(viewer);

					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(viewer, Minigames.PREFIX + "The minimum players value must be an integer.");
					return AnvilGUI.Response.close();
				}
			})));

		contents.set(2, 8, ClickableItem.of(new ItemBuilder(Material.GLASS)
				.name("&eVisibility")
				.lore("&7Sets the visibility of", "&7the team's name tags", "", "&3Current Value:", "&e" + StringUtils.camelCase(team.getNameTagVisibility())),
			e -> new TeamVisibilityMenu(arena, team).open(viewer)));
	}

}
