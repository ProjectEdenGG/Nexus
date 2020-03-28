package me.pugabyte.bncore.utils;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Arrays;

public enum WorldGroup {
	SURVIVAL("world", "world_nether", "world_the_end", "staff_world", "staff_world_nether", "staff_world_the_end", "safepvp", "wither"),
	CREATIVE("creative", "buildcontest", "buildadmin", "jail", "pirate", "tiger"),
	MINIGAMES("gameworld", "blockball"),
	SKYBLOCK("skyblock", "skyblock_nether"),
	ADVENTURE("stranded"),
	EVENT("2y"),
	UNKNOWN;

	private String[] worlds;

	WorldGroup() {}

	WorldGroup(String... worlds) {
		this.worlds = worlds;
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(name());
	}

	public String[] getWorlds() {
		return worlds;
	}

	public static WorldGroup get(Entity entity) {
		return get(entity.getWorld());
	}

	public static WorldGroup get(World world) {
		return get(world.getName());
	}

	public static WorldGroup get(String world) {
		for (WorldGroup group : values())
			if (group.getWorlds() != null)
				if (Arrays.asList(group.getWorlds()).contains(world))
					return group;

		return UNKNOWN;
	}
}
