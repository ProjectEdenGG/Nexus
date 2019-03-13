package me.pugabyte.bncore.features.minigames;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.commands.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.listeners.MatchListener;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Minigames {
	public static final String PREFIX = BNCore.getPrefix("Minigames");
	@Getter
	private static World gameworld;
	@Getter
	private static Location gamelobby;

	public Minigames() {
		new MinigamesCommand();
		new MatchListener();

		gameworld = Bukkit.getWorld("gameworld");
		gamelobby = new Location(gameworld, 1861.5, 38.1, 247.5, 0, 0);

		try (Stream<Path> paths = Files.walk(Paths.get("plugins/BNCore/minigames/arenas"))) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					new Arena.Reader(filePath.getFileName().toString().replace(".yml", ""));
				}
			});
		} catch (IOException ex) {
			BNCore.severe("An error occurred while trying to read arena configuration files: " + ex.getMessage());
		}

	/*
		{
			Loadout loadout = Loadout.builder().inventoryContents(new ItemStack[]{new ItemStack(Material.SNOWBALL, 64)}).build();
			Location lobbyLocation = new Location(gameworld, -1317.5, 14.1, -963.5, 180, 0);

			List<Location> spawnpointsRed = new ArrayList<>();
			spawnpointsRed.add(new Location(gameworld, -1223.5, 7.1, -1170.5, 0, 0));
			spawnpointsRed.add(new Location(gameworld, -1216, 7.1, -1111, 180, 0));

			List<Location> spawnpointsBlue = new ArrayList<>();
			spawnpointsBlue.add(new Location(gameworld, -1130.5, 7.1, -1170.5, 0, 0));
			spawnpointsBlue.add(new Location(gameworld, -1138, 7.1, -1111, 180, 0));

			List<Team> teams = new ArrayList<>();
			teams.add(Team.builder().color(ChatColor.RED).name(ChatColor.RED + "Red").objective("Kill Blue with snowballs").loadout(loadout).spawnpoints(spawnpointsRed).build());
			teams.add(Team.builder().color(ChatColor.BLUE).name(ChatColor.BLUE + "Blue").objective("Kill Red with snowballs").loadout(loadout).spawnpoints(spawnpointsBlue).build());

			Lobby lobby = new Lobby(lobbyLocation, 5);
			Arena arena = new Arena(1, "ArcticCombat","Arctic Combat", teams, Mechanics.PAINTBALL.get(), lobby);
			arena.setMinPlayers(2);
			arena.setSeconds(20);
			arena.setWinningScore(2);
			arena.canJoinLate(true);
			arena.setRespawnLocation(new Location(gameworld, -1176.5, 35.1, -1150.5, 0, 0));

			ArenaManager.add(arena);
		}

		{
			ItemStack axe = new ItemStack(Material.IRON_AXE);
			ItemStack bow = new ItemStack(Material.BOW);
			ItemStack arrow = new ItemStack(Material.ARROW);

			Loadout loadout = Loadout.builder().inventoryContents(new ItemStack[]{axe, bow, arrow}).build();
			Location lobbyLocation = new Location(gameworld, 580.5, 5.1, 141.5, -180, 0);

			List<Location> spawnpoints = new ArrayList<>();
			spawnpoints.add(new Location(gameworld, 686.5, 4.1, 198.5, 90, 0));
			spawnpoints.add(new Location(gameworld, 640.5, 4.1, 243.5, -180, 0));
			spawnpoints.add(new Location(gameworld, 595.5, 4.1, 198.5, -90, 0));
			spawnpoints.add(new Location(gameworld, 640.5, 4.1, 153.5, 0, 0));

			List<Team> teams = new ArrayList<>();
			teams.add(Team.builder().color(ChatColor.WHITE).name(ChatColor.WHITE + "Default").objective("Kill players").loadout(loadout).spawnpoints(spawnpoints).build());

			Lobby lobby = new Lobby(lobbyLocation, 5);
			Arena arena = new Arena("Lab13", "Lab 13", teams, Mechanics.ONE_IN_THE_QUIVER.get(), lobby);
			arena.setMinPlayers(2);
			arena.setSeconds(60);
			arena.setWinningScore(6);
			arena.setRespawnLocation(new Location(gameworld, 640.5, 13.1, 205.5, 90, 0));

			ArenaManager.add(arena);
		}

		{
			ItemStack axe = new ItemStack(Material.DIAMOND_SWORD);
			ItemStack bow = new ItemStack(Material.BOW);
			bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
			ItemStack arrow = new ItemStack(Material.ARROW);

			Loadout loadout = Loadout.builder().inventoryContents(new ItemStack[]{axe, bow, arrow}).build();

			Location lobbyLocation = new Location(gameworld, -1009.5, 30.1, 965.5, 90, 0);

			List<Location> spawnpointsRed = new ArrayList<>();
			spawnpointsRed.add(new Location(gameworld, -980.5, 36.1, 1020.5, -90, 0));
			spawnpointsRed.add(new Location(gameworld, -974.5, 35.1, 1008, -90, 0));

			List<Location> spawnpointsBlue = new ArrayList<>();
			spawnpointsBlue.add(new Location(gameworld, -888.5, 35.1, 1012.5, 90, 0));
			spawnpointsBlue.add(new Location(gameworld, -882.5, 36.1, 1000, 90, 0));

			List<Team> teams = new ArrayList<>();
			teams.add(Team.builder().color(ChatColor.RED).name(ChatColor.RED + "Red").objective("Take blue's flag and capture it at your base").loadout(loadout).spawnpoints(spawnpointsRed).build());
			teams.add(Team.builder().color(ChatColor.BLUE).name(ChatColor.BLUE + "Blue").objective("Take red's flag and capture it at your base").loadout(loadout).spawnpoints(spawnpointsBlue).build());

			Lobby lobby = new Lobby(lobbyLocation, 5);
			Arena arena = new Arena("AngelsValley", "Angels Valley", teams, Mechanics.CAPTURE_THE_FLAG.get(), lobby);
			arena.setMinPlayers(2);
			arena.setSeconds(60);
			arena.setWinningScore(2);
			arena.canJoinLate(true);
			arena.setRespawnLocation(new Location(gameworld, -932, 57.1, 1010.5, 0, 90));

			ArenaManager.add(arena);
		}
	*/

	}

}
