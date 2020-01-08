package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ArenaManager {
	private static List<Arena> arenas = new ArrayList<>();
	private static String folder = "plugins/BNCore/minigames/arenas/";

	public static List<Arena> getAll() {
		return arenas;
	}

	public static List<String> getNames() {
		return getNames(null);
	}

	public static List<String> getNames(String filter) {
		List<String> names = new ArrayList<>();
		for (Arena arena : arenas) {
			if (filter != null)
				if (!arena.getName().toLowerCase().startsWith(filter.toLowerCase()))
					continue;
			names.add(arena.getName());
		}

		return names;
	}

	public static Arena find(String name) {
		for (Arena arena : arenas)
			if (arena.getName().toLowerCase().startsWith(name.toLowerCase()))
				return arena;
		throw new InvalidInputException("Arena not found");
	}

	public static Arena get(String name) {
		for (Arena arena : arenas)
			if (arena.getName().equalsIgnoreCase(name))
				return arena;
		throw new InvalidInputException("Arena not found");
	}

	public static Arena get(int id) {
		for (Arena arena : arenas)
			if (arena.getId() == id)
				return arena;
		throw new InvalidInputException("Arena not found");
	}

	public static void add(Arena arena) {
		try { arenas.remove(get(arena.getId()));   } catch (InvalidInputException ignore) {}
		try { arenas.remove(get(arena.getName())); } catch (InvalidInputException ignore) {}
		arenas.add(arena);
	}

	private static String getFile(String name) {
		return folder + name + ".yml";
	}

	private static FileConfiguration getConfig(String name) {
		File file = new File(getFile(name));
		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					BNCore.warn("File " + file.getName() + " already exists");
			} catch (IOException ex) {
				BNCore.severe("An error occurred while trying to create a configuration file: " + ex.getMessage());
			}
		}

		return YamlConfiguration.loadConfiguration(file);
	}

	public static void read() {
		try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
			paths.forEach(filePath -> {
				if (!Files.isRegularFile(filePath)) return;

				String name = filePath.getFileName().toString();
				if (name.startsWith(".")) return;
				if (!name.endsWith(".yml")) return;

				read(name.replace(".yml",""));
			});
		} catch (IOException ex) {
			BNCore.severe("An error occurred while trying to read arena configuration files: " + ex.getMessage());
		}
	}

	public static void read(String name) {
		add((Arena) getConfig(name).get("arena"));
		BNCore.log("Loaded arena " + name);
	}

	public static void write() {
		arenas.forEach(ArenaManager::write);
	}

	public static void write(Arena arena) {
		FileConfiguration arenaConfig = getConfig(arena.getName());
		arenaConfig.set("arena", arena);

		try {
			arenaConfig.save(getFile(arena.getName()));
		} catch (IOException ex) {
			BNCore.severe("An error occurred while trying to write arena configuration files: " + ex.getMessage());
		}
	}

	public static Arena getFromRegion(String regionName) {
		try {
			String mechanicName = regionName.split("_")[0];
			String arenaName = regionName.split("_")[1];
			Arena arena = get(arenaName);
			if (arena.getMechanic().getName().equalsIgnoreCase(mechanicName))
				return arena;
		} catch (ArrayIndexOutOfBoundsException ignore) {}

		return null;
	}

}
