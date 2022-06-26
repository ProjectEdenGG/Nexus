package gg.projecteden.nexus.features.survival;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Commander;
import nl.pim16aap2.bigDoors.Door;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
	TODO:
	 	- Abstract out to not just survival world
	 		- add some custom settings saved in mongo
	 		- add some conditions such as:
				- time of day, keep door open/closed
				- grace period, keep door open/closed for x seconds
 */
public class BigDoorOpener {
	@Getter
	private static final BigDoors bigDoors = Nexus.getBigDoors();
	private static final Commander commander = bigDoors.getCommander();
	private static final Map<Long, LocalDateTime> gracePeriodDoors = new ConcurrentHashMap<>();
	private static final long gracePeriod = TickTime.SECOND.x(5);

	static {
		Tasks.repeat(0, TickTime.SECOND, () -> {
			// Grace Period
			if (!gracePeriodDoors.isEmpty()) {
				for (Long id : new HashSet<>(gracePeriodDoors.keySet())) {
					if (Duration.between(gracePeriodDoors.get(id), LocalDateTime.now()).toSeconds() >= gracePeriod)
						gracePeriodDoors.remove(id);
				}
			}
		});
	}

	public static Door getDoor(long id) {
		return commander.getDoor(null, id);
	}

	public static void toggleDoor(Door door) {
		toggleDoor(door.getDoorUID());
	}

	public static void toggleDoor(long id) {
		bigDoors.toggleDoor(id);
	}

	public static boolean isDoorBusy(Door door) {
		return commander.isDoorBusy(door.getDoorUID()) || gracePeriodDoors.containsKey(door.getDoorUID());
	}

	public static void tryToggleDoor(ProtectedRegion toggleRegion, Player player, String baseRegion, DoorAction state) {
		if (PlayerUtils.isVanished(player) || GameModeWrapper.of(player).is(GameMode.SPECTATOR))
			return;

		WorldGuardUtils WGUtils = new WorldGuardUtils(player);

		if (!WGUtils.getRegionsLike(baseRegion + "_bigdoor_[0-9]+").contains(toggleRegion))
			return;

		int playersInRegion = WGUtils.getPlayersInRegion(toggleRegion).stream()
			.filter(_player -> !PlayerUtils.isSelf(_player, player))
			.toList()
			.size();

		if (playersInRegion > 0)
			return;

		int doorId = Integer.parseInt(toggleRegion.getId().replaceAll(baseRegion + "_bigdoor_", "").trim());
		tryToggleDoor(state, doorId);
	}

	private static void tryToggleDoor(DoorAction state, long doorId) {
		Door door = getDoor(doorId);
		if (door == null || door.isLocked())
			return;

		// Try again later
		if (isDoorBusy(door)) {
			Tasks.wait(TickTime.SECOND.x(2), () -> tryToggleDoor(state, doorId));
			return;
		}

		boolean opened = door.isOpen();
		if (state == DoorAction.OPEN) {
			if (opened)
				return;
		} else {
			if (!opened)
				return;
		}

		gracePeriodDoors.put(doorId, LocalDateTime.now());
		BigDoorOpener.toggleDoor(door);
	}


	public enum DoorAction {
		OPEN,
		CLOSE,
		;
	}
}
