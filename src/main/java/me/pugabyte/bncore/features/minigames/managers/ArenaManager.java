package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.features.minigames.models.Arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArenaManager {
	private static List<Arena> arenas = new ArrayList<>();

	public static Optional<Arena> get(String name) {
		return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst();
	}

	public static void add(Arena arena) {
		arenas.add(arena);
	}
}
