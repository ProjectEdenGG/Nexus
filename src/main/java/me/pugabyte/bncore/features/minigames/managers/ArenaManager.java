package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.features.minigames.models.Arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArenaManager {
	private static List<Arena> arenas = new ArrayList<>();

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
			if (arena.getName().startsWith(filter))
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
		Optional<Arena> previous;
		previous = get(arena.getId());
		previous.ifPresent(arenas::remove);
		previous = get(arena.getName());
		previous.ifPresent(arenas::remove);
		arenas.add(arena);
	}

}
