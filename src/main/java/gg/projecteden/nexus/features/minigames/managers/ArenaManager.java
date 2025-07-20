package gg.projecteden.nexus.features.minigames.managers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.events.arenas.AllArenasLoadedEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArenaManager {
	@Getter
	private static final List<Arena> arenas = new ArrayList<>();
	private static final String FOLDER = "plugins/Nexus/minigames/arenas/";
	public static boolean LOADED = false;

	public static List<Arena> getAll() {
		return arenas;
	}

	public static List<Arena> getAllEnabled() {
		return arenas.stream().filter(arena -> !arena.isTestMode()).collect(Collectors.toList());
	}

	public static Stream<Arena> getAllStream(@Nullable String filter) {
		Stream<Arena> stream = arenas.stream();
		if (filter != null) {
			final String lowerFilter = filter.toLowerCase();
			stream = stream.filter(arena -> arena.getName().toLowerCase().startsWith(lowerFilter));
		}
		return stream;
	}

	public static List<Arena> getAll(@Nullable String filter) {
		return getAllStream(filter).collect(Collectors.toList());
	}

	public static List<Arena> getAll(@Nullable MechanicType mechanic) {
		return arenas.stream().filter(arena -> mechanic == null || mechanic == arena.getMechanicType()).collect(Collectors.toList());
	}

	public static List<Arena> getAllEnabled(@Nullable MechanicType mechanic) {
		return getAllEnabled().stream().filter(arena -> mechanic == null || mechanic == arena.getMechanicType()).collect(Collectors.toList());
	}

	public static Stream<String> getNamesStream(@Nullable String filter) {
		return getAll(filter).stream().map(Arena::getName);
	}

	public static List<String> getNames(@Nullable String filter) {
		return getNamesStream(filter).collect(Collectors.toList());
	}

	public static List<String> getNames() {
		return getNames(null);
	}

	public static Arena getFromLocation(Location location) {
		return getFromLocation(location, null);
	}

	public static Arena getFromLocation(Location location, String type) {
		Set<ProtectedRegion> regionsAt = new WorldGuardUtils(location).getRegionsAt(location);
		for (ProtectedRegion region : regionsAt) {
			Arena fromRegion = getFromRegion(region.getId());
			if (fromRegion != null && (type == null || fromRegion.ownsRegion(region, type)))
				return fromRegion;
		}

		return null;
	}

	public static Arena getFromRegion(String regionName) {
		try {
			String mechanicName, arenaName;
			if (!regionName.contains("_")) {
				mechanicName = regionName;
				arenaName = regionName;
			} else {
				mechanicName = regionName.split("_")[0];
				arenaName = regionName.split("_")[1];
			}

			Arena arena = get(arenaName);
			if (arena.getMechanic().getClass().getSimpleName().equalsIgnoreCase(mechanicName))
				return arena;
		} catch (ArrayIndexOutOfBoundsException | InvalidInputException ignore) {}

		return null;
	}

	public static Arena find(String name) {
		if (Nullables.isNotNullOrEmpty(name)) {
			for (Arena arena : arenas)
				if (arena.getName().equalsIgnoreCase(name))
					return arena;
			for (Arena arena : arenas)
				if (arena.getName().toLowerCase().startsWith(name.toLowerCase()))
					return arena;
		}
		throw new InvalidInputException("Arena not found");
	}

	public static  boolean exists(String name) {
		try {
			get(name);
			return true;
		} catch (InvalidInputException ex) {
			return false;
		}
	}

	public static Arena get(String name) {
		arenas.removeIf(Objects::isNull);
		for (Arena arena : arenas)
			if (arena.getName().equalsIgnoreCase(name))
				return arena;
		throw new InvalidInputException("Arena not found");
	}

	public static Arena get(int id) {
		arenas.removeIf(Objects::isNull);
		for (Arena arena : arenas)
			if (arena.getId() == id)
				return arena;
		throw new InvalidInputException("Arena not found");
	}

	public static void add(Arena arena) {
		try { arenas.remove(get(arena.getId()));   } catch (NullPointerException | InvalidInputException ignore) {}
		try { arenas.remove(get(arena.getName())); } catch (NullPointerException | InvalidInputException ignore) {}
		arenas.add(arena);
	}

	public static void remove(Arena arena) {
		arenas.remove(arena);
	}

	private static String getFile(String name) {
		return FOLDER + name + ".yml";
	}

	private static FileConfiguration getConfig(String name) {
		File file = new File(getFile(name));
		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					Nexus.warn("File " + file.getName() + " already exists");
			} catch (IOException ex) {
				Nexus.severe("An error occurred while trying to create a configuration file: " + ex.getMessage());
				Debug.log(ex);
			}
		}

		return YamlConfiguration.loadConfiguration(file);
	}

	@SneakyThrows
	public static void read() {
		File file = Paths.get(FOLDER).toFile();
		if (!file.exists()) file.createNewFile();
		arenas.clear();
		try (Stream<Path> paths = Files.walk(Paths.get(FOLDER))) {
			paths.forEach(filePath -> {
				try {
					if (!Files.isRegularFile(filePath)) return;

					String name = filePath.getFileName().toString();
					if (name.startsWith(".")) return;
					if (!name.endsWith(".yml")) return;

					read(name.replace(".yml", ""));
				} catch (Exception ex) {
					Nexus.severe("An error occurred while trying to read arena configuration file " + filePath.getFileName().toFile(), ex);
					Debug.log(ex);
				}
			});
			ArenaManager.LOADED = true;
			Tasks.sync(() -> new AllArenasLoadedEvent().callEvent());
		} catch (Exception ex) {
			Nexus.severe("An error occurred while trying to read arena configuration files: " + ex.getMessage());
			Debug.log(ex);
		}
	}

	public static void read(String name) {
		try {
			Arena arena = (Arena) getConfig(name).get("arena");
			if (arena == null)
				throw new NullPointerException();
			add(arena);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write() {
		arenas.forEach(ArenaManager::write);
	}

	public static void write(Arena arena) {
		FileConfiguration arenaConfig = getConfig(arena.getName());
		arenaConfig.set("arena", arena);

		try {
			arenaConfig.save(getFile(arena.getName()));
			if (!arenas.contains(arena))
				add(arena);
		} catch (Exception ex) {
			Nexus.severe("An error occurred while trying to write arena configuration file " + arena.getName() + ": " + ex.getMessage());
			Debug.log(ex);
		}
	}

	public static void delete(String arena) {
		delete(get(arena));
	}

	public static void delete(Arena arena) {
		File file = new File(getFile(arena.getName()));
		if (!file.exists()) {
			return;
		}

		file.delete();
		remove(arena);
	}

	public static int getNextId() {
		int id = 1;
		for (Arena arena : getAll())
			if (arena.getId() >= id)
				id = (arena.getId() + 1);

		return id;
	}

	@SneakyThrows
	public static <T> T convert(Arena arena, Class<?> clazz) {
		return (T) clazz.getDeclaredConstructor(Map.class).newInstance(arena.serialize());
	}

}
