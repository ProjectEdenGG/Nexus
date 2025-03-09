package gg.projecteden.nexus.features.bigdoors;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfig;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfig.DoorAction;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfigService;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.WorldUtils;
import gg.projecteden.nexus.utils.WorldUtils.TimeQuadrant;
import lombok.Data;
import lombok.Getter;
import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Commander;
import nl.pim16aap2.bigDoors.Door;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
	TODO:
		- time of day, keep door open/closed
 */
public class BigDoorManager extends Feature implements Listener {
	@Data
	public static class NamedBigDoor {
		private final String name;
	}

	private static final BigDoorConfigService bigDoorConfigService = new BigDoorConfigService();

	@Getter
	private static final BigDoors bigDoors = Nexus.getBigDoors();
	@Getter
	private static final Commander commander = bigDoors.getCommander();

	private static final List<Long> gracePeriodDoors = new ArrayList<>();

	private void debug(Player debugger, String debug) {
		if (!debugger.isOnline())
			return;

		debugger.sendMessage(debug);
	}

	@Override
	public void onStart() {
		new Listeners();

		new BigDoorConfigService().cacheAll();

		Tasks.repeat(0, TickTime.SECOND, () -> {
			// Queued Actions
			for (BigDoorConfig bigDoorConfig : bigDoorConfigService.getCache().values()) {

				DoorAction queuedDoorAction = bigDoorConfig.getQueuedDoorAction();
				if (queuedDoorAction == null)
					continue;

				if (isDoorBusy(bigDoorConfig) || gracePeriodDoors.contains(bigDoorConfig.getDoorId()))
					continue;

				bigDoorConfig.setQueuedDoorAction(null);
				bigDoorConfigService.save(bigDoorConfig);

//				Dev.WAKKA.send("Applying queued action: " + queuedDoorAction);
				tryToggleDoor(bigDoorConfig, queuedDoorAction, Dev.WAKKA.getPlayer());
			}

			// Time of Day
//			for (BigDoorConfig bigDoorConfig : bigDoorConfigService.getAll()) {
//				checkDoorTimeQuadrants(bigDoorConfig, Dev.WAKKA.getPlayer());
//			}

			// Grace Period
			if (!gracePeriodDoors.isEmpty()) {
				for (Long doorId : new ArrayList<>(gracePeriodDoors)) {
					BigDoorConfig bigDoorConfig = bigDoorConfigService.fromDoorId(doorId);
					if (bigDoorConfig == null || bigDoorConfig.getGracePeriodStart() == null) {
						gracePeriodDoors.remove(doorId);
						continue;
					}

					if (Duration.between(bigDoorConfig.getGracePeriodStart(), LocalDateTime.now()).toSeconds() >= bigDoorConfig.getGracePeriodSeconds()) {
						if (commander.isDoorBusy(bigDoorConfig.getDoorId()))
							continue;

//						Dev.WAKKA.send("Removing door " + bigDoorConfig.getDoorName() + " from grace period list");

						gracePeriodDoors.remove(doorId);

						bigDoorConfig.setGracePeriodStart(null);
						bigDoorConfigService.save(bigDoorConfig);
					}
				}
			}
		});
	}

	public static Set<Door> getDoors() {
		return getCommander().getDoors();
	}

	public static @Nullable Door getDoor(long id) {
		for (Door door : getDoors()) {
			if (door.getDoorUID() == id)
				return door;
		}

		return null;
	}

	public static @Nullable Door getDoor(String doorName) {
		for (Door door : getDoors()) {
			if (door.getName().equalsIgnoreCase(doorName))
				return door;
		}

		return null;
	}

	public static void toggleDoor(Door door) {
		toggleDoor(door.getDoorUID());
	}

	public static void toggleDoor(long id) {
		bigDoors.toggleDoor(id);
	}

	public static boolean isDoorBusy(BigDoorConfig bigDoorConfig) {
		return commander.isDoorBusy(bigDoorConfig.getDoorId());
	}

	//

	public static void tryToggleDoor(ProtectedRegion toggleRegion, Player player, DoorAction state) {
		if (Vanish.isVanished(player) || GameMode.SPECTATOR == player.getGameMode()) {
			return;
		}

		WorldGuardUtils WGUtils = new WorldGuardUtils(player);

		if (!WGUtils.getRegionsLike(".*_bigdoor_[0-9]+").contains(toggleRegion)) {
			return;
		}

		int doorId = Integer.parseInt(toggleRegion.getId().replaceAll(".*_bigdoor_", "").trim());

		BigDoorConfig bigDoorConfig = bigDoorConfigService.fromDoorId(doorId);
		if (bigDoorConfig == null) {
			return;
		}

		if (bigDoorConfig.getPlayerInToggleRegion(player).size() > 0) {
			return;
		}

		tryToggleDoor(bigDoorConfig, state, player);
	}

	private static void tryToggleDoor(BigDoorConfig bigDoorConfig, DoorAction action, Player debugger) {
		Door door = bigDoorConfig.getDoor();
		if (door == null || door.isLocked()) {
			return;
		}

		if (isDoorBusy(bigDoorConfig)) {
			queueAction(bigDoorConfig, action, debugger);
			return;
		}

		boolean opened = door.isOpen();
		if (action == DoorAction.OPEN) {
			if (opened) {
				return;
			}
		} else {
			if (!opened) {
				return;
			}
		}

//		debugger.sendMessage("Applying action to door: " + action);
		bigDoorConfig.setGracePeriodStart(LocalDateTime.now());

		gracePeriodDoors.add(bigDoorConfig.getDoorId());
		toggleDoor(door);

//		checkDoorTimeQuadrants(bigDoorConfig, debugger);

		bigDoorConfigService.save(bigDoorConfig);
	}

	private static void checkDoorTimeQuadrants(BigDoorConfig bigDoorConfig, Player debugger) {
		if (bigDoorConfig.getTimeState().isEmpty() || bigDoorConfig.getTimeQueuedDoorAction() != null)
			return;

		TimeQuadrant worldQuadrant = WorldUtils.TimeQuadrant.of(bigDoorConfig.getDoor().getWorld());
		for (TimeQuadrant doorQuadrant : bigDoorConfig.getTimeState().keySet()) {
			if (worldQuadrant == doorQuadrant) {
				DoorAction doorQuadrantAction = bigDoorConfig.getTimeState().get(doorQuadrant);
				bigDoorConfig.setTimeQueuedDoorAction(doorQuadrantAction);

//				debugger.sendMessage("Door Time Quadrant " + doorQuadrant + " is toggling door " + doorQuadrantAction);
				tryToggleDoor(bigDoorConfig, doorQuadrantAction, debugger);

				bigDoorConfig.setTimeQueuedDoorAction(null);
				bigDoorConfigService.save(bigDoorConfig);
				break;
			}
		}
	}

	private static void queueAction(BigDoorConfig bigDoorConfig, DoorAction action, Player debugger) {
//		if(!bigDoorConfig.getTimeState().isEmpty() && bigDoorConfig.getTimeQueuedDoorAction() == null){
//			TimeQuadrant worldQuadrant = WorldUtils.TimeQuadrant.of(bigDoorConfig.getDoor().getWorld());
//			for (TimeQuadrant doorQuadrant : bigDoorConfig.getTimeState().keySet()) {
//				if (worldQuadrant == doorQuadrant) {
//					DoorAction doorQuadrantAction = bigDoorConfig.getTimeState().get(doorQuadrant);
//					if (doorQuadrantAction != action) {
//						debugger.sendMessage("QueueAction: " + action + " != doorQuadAction: " + doorQuadrantAction);
//						return;
//					}
//
//					bigDoorConfig.setTimeQueuedDoorAction(doorQuadrantAction);
//					break;
//				}
//			}
//		}

		bigDoorConfig.setQueuedDoorAction(action);
		bigDoorConfigService.save(bigDoorConfig);

//		debugger.sendMessage("door is busy, queuing action: " + action);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		BigDoorManager.tryToggleDoor(event.getRegion(), event.getPlayer(), DoorAction.OPEN);
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		BigDoorManager.tryToggleDoor(event.getRegion(), event.getPlayer(), DoorAction.CLOSE);
	}
}
