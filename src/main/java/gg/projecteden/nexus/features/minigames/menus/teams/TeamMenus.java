package gg.projecteden.nexus.features.minigames.menus.teams;

import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.minigames.menus.teams.loadout.DeleteLoadoutMenu;
import gg.projecteden.nexus.features.minigames.menus.teams.loadout.LoadoutMenu;
import gg.projecteden.nexus.features.minigames.menus.teams.loadout.PotionEffectEditorMenu;
import gg.projecteden.nexus.features.minigames.menus.teams.loadout.PotionEffectsMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class TeamMenus {

	public void openTeamsMenu(Player player, Arena arena) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamsMenu")
				.title("Teams Menu")
				.provider(new TeamsMenu(arena))
				.size(2, 9)
				.build();
		INV.open(player);
	}

	public void openTeamsEditorMenu(Player player, Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamEditorMenu")
				.title("Team Editor Menu")
				.provider(new TeamEditorMenu(arena, team))
				.size(3, 9)
				.build();
		INV.open(player);
	}

	public void openTeamsColorMenu(Player player, Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamColorMenu")
				.title("Team Color Menu")
				.provider(new TeamColorMenu(arena, team))
				.size(3, 9)
				.build();
		INV.open(player);
	}

	public void openDeleteTeamMenu(Player player, Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamDeleteMenu")
				.title("Delete Team?")
				.provider(new DeleteTeamMenu(arena, team))
				.size(3, 9)
				.build();
		INV.open(player);
	}

	public void openLoadoutMenu(Player player, Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamLoadoutMenu")
				.title("Loadout Menu")
				.provider(new LoadoutMenu(arena, team))
				.size(6, 9)
				.build();
		INV.open(player);
	}

	public void openPotionEffectsMenu(Player player, Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamPotionMenu")
				.title("Potion Effects Menu")
				.provider(new PotionEffectsMenu(arena, team))
				.size(3, 9)
				.build();
		INV.open(player);
	}

	public void openDeleteLoadoutMenu(Player player, Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamDeleteLoadoutMenu")
				.title("Delete Loadout?")
				.provider(new DeleteLoadoutMenu(arena, team))
				.size(3, 9)
				.build();
		INV.open(player);
	}

	public void openPotionEffectEditorMenu(Player player, Arena arena, Team team, PotionEffect potionEffect) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamPotionEffectEditorMenu")
				.title("Potion Effect Editor Menu")
				.provider(new PotionEffectEditorMenu(arena, team, potionEffect))
				.size(6, 9)
				.build();
		INV.open(player);
	}

	public SmartInventory openSpawnpointMenu(Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamSpawnpointLocationsMenu")
				.title("Spawnpoint Location Menus")
				.provider(new SpawnpointLocationsMenu(arena, team))
				.size(6, 9)
				.build();
		return INV;
	}

	public void openTeamsVisibilityMenu(Player player, Arena arena, Team team) {
		SmartInventory INV = SmartInventory.builder()
				.id("teamVisibilityMenu")
				.title("Team Visibility Menu")
				.provider(new TeamVisibilityMenu(arena, team))
				.size(2, 9)
				.build();
		INV.open(player);
	}

}
