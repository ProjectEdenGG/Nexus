package gg.projecteden.nexus.features.survival;

import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Survival extends Feature implements Listener {
	@Getter
	private static final String spawnRegion = "spawn";

	@Override
	public void onStart() {
		new DecorationStore();
	}

	@NotNull
	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	@NonNull
	public static WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	public static World getWorld() {
		return Bukkit.getWorld("survival");
	}

	public static WorldGroup getWorldGroup() {
		return WorldGroup.SURVIVAL;
	}

	public static @NotNull Collection<Player> getPlayersAtSpawn() {
		return worldguard().getPlayersInRegion(getSpawnRegion());
	}

	public static boolean isNotAtSpawn(Player player) {
		return isNotAtSpawn(player.getLocation());
	}

	public static boolean isNotAtSpawn(Location location) {
		return !isAtSpawn(location);
	}

	public static boolean isAtSpawn(Player player) {
		return isAtSpawn(player.getLocation());
	}

	public static boolean isAtSpawn(Location location) {
		if (!location.getWorld().equals(getWorld()))
			return false;

		return worldguard().isInRegion(location, spawnRegion);
	}

	public static boolean isInWorldGroup(Player player) {
		return WorldGroup.of(player) == getWorldGroup();
	}
}
