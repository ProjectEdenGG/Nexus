package gg.projecteden.nexus.features.survival;

import gg.projecteden.nexus.features.bigdoors.BigDoorManager;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfig.DoorAction;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class Survival extends Feature implements Listener {
	@Getter
	private static final String spawnRegion = "spawn";

	@NotNull
	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public static World getWorld() {
		return Bukkit.getWorld("survival");
	}

	public static WorldGroup getWorldGroup() {
		return WorldGroup.SURVIVAL;
	}

	public static boolean isNotAtSpawn(Player player) {
		return isNotAtSpawn(player.getLocation());
	}

	public static boolean isNotAtSpawn(Location location) {
		return !isAtSpawn(location);
	}

	public static boolean isAtSpawn(Location location) {
		if (!location.getWorld().equals(getWorld()))
			return false;

		return worldguard().isInRegion(location, spawnRegion);
	}

	public static boolean isInWorldGroup(Player player) {
		return WorldGroup.of(player) == getWorldGroup();
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		BigDoorManager.tryToggleDoor(event.getRegion(), event.getPlayer(), Survival.getSpawnRegion(), DoorAction.OPEN);
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		BigDoorManager.tryToggleDoor(event.getRegion(), event.getPlayer(), Survival.getSpawnRegion(), DoorAction.CLOSE);
	}
}
