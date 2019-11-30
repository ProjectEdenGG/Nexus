package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ArenaManager {
	private static List<Arena> arenas = new ArrayList<>();
	private static String folder = "plugins/BNCore/minigames/arenas/";

	public static List<Arena> getAll() {
		return arenas;
	}

	public static List<String> getNames() {
		List<String> names = new ArrayList<>();
		for (Arena arena : arenas)
			names.add(arena.getName());
		return names;
	}

	public static List<String> getNames(String filter) {
		List<String> names = new ArrayList<>();
		for (Arena arena : arenas)
			if (arena.getName().toLowerCase().startsWith(filter.toLowerCase()))
				names.add(arena.getName());
		return names;
	}

	public static Optional<Arena> get(String name) {
		return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst();
	}

	public static Optional<Arena> get(int id) {
		return arenas.stream().filter(arena -> arena.getId() == id).findFirst();
	}

	public static void add(Arena arena) {
		get(arena.getId()).ifPresent(arenas::remove);
		get(arena.getName()).ifPresent(arenas::remove);
		arenas.add(arena);
	}

	private static String getFile(String name) {
		return folder + name + ".yml";
	}

	private static FileConfiguration getConfig(String name) {
		return YamlConfiguration.loadConfiguration(new File(getFile(name)));
	}

	public static void read() {
		try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
			paths.forEach(filePath -> {
				if (!Files.isRegularFile(filePath)) return;

				String name = filePath.getFileName().toString().replace(".yml", "");
				read(name);
			});
		} catch (IOException ex) {
			BNCore.severe("An error occurred while trying to read arena configuration files: " + ex.getMessage());
		}
	}

	public static void read(String name) {
		add((Arena) getConfig(name).get("arena"));
	}

	public static void write() {
		arenas.forEach(arena -> write(arena.getName()));
	}

	public static void write(String name) {
		Optional<Arena> optionalArena = get(name);
		if (!optionalArena.isPresent()) return;

		FileConfiguration arenaConfig = getConfig(name);
		arenaConfig.set("arena", optionalArena.get());

		try {
			arenaConfig.save(getFile(name));
		} catch (IOException ex) {
			BNCore.severe("An error occurred while trying to write arena configuration files: " + ex.getMessage());
		}
	}

}
