package me.pugabyte.nexus.features.regionapi;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.papermc.paper.event.entity.EntityMoveEvent;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.regionapi.events.RegionEventFactory;
import me.pugabyte.nexus.features.regionapi.events.common.EnteredRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.common.EnteringRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.common.LeavingRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.common.LeftRegionEvent;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class WorldGuardRegionAPI extends Feature implements Listener {
	private static final Map<Entity, Set<ProtectedRegion>> entities = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		updateRegions(event.getPlayer(), MovementType.RESPAWN, event.getPlayer().getLocation(), event);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		clearRegions(event.getPlayer(), MovementType.DISCONNECT, event);
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		clearRegions(event.getPlayer(), MovementType.WORLD_CHANGE, event);
		updateRegions(event.getPlayer(), MovementType.WORLD_CHANGE, event.getPlayer().getLocation(), event);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		TeleportCause cause = event.getCause();
		MovementType movementType = MovementType.TELEPORT;

		if (cause == TeleportCause.END_PORTAL || cause == TeleportCause.NETHER_PORTAL) {
			clearRegions(event.getPlayer(), MovementType.WORLD_CHANGE, event);
			movementType = MovementType.WORLD_CHANGE;
		}

		updateRegions(event.getPlayer(), movementType, event.getTo(), event);
	}

	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent event) {
		MovementType movementType = MovementType.TELEPORT;
		updateRegions(event.getEntity(), movementType, event.getTo(), event);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		updateRegions(event.getPlayer(), MovementType.MOVE, event.getTo(), event);
	}

	@EventHandler
	public void onEntityMove(EntityMoveEvent event) {
		updateRegions(event.getEntity(), MovementType.MOVE, event.getTo(), event);
	}

	@EventHandler
	public void onVehicleMove(VehicleMoveEvent event) {
		if (event.getVehicle().getPassengers().isEmpty())
			return;

		for (Entity entity : event.getVehicle().getPassengers())
			updateRegions(entity, MovementType.RIDE, entity.getLocation(), event);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		updateRegions(event.getPlayer(), MovementType.RESPAWN, event.getRespawnLocation(), event);
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		updateRegions(event.getEntity(), MovementType.SPAWN, event.getLocation(), event);
	}

	public static Set<ProtectedRegion> getRegions(Entity entity) {
		return entities.getOrDefault(entity, new HashSet<>());
	}

	private void clearRegions(Entity entity, MovementType movementType, PlayerEvent event) {
		Set<ProtectedRegion> regions = getRegions(entity);
		entities.remove(entity);
		if (regions == null)
			return;

		for (ProtectedRegion region : regions) {
			RegionEventFactory.of(LeavingRegionEvent.class, region, entity, movementType, event).callEvent();
			RegionEventFactory.of(LeftRegionEvent.class, region, entity, movementType, event).callEvent();
		}
	}

	private synchronized void updateRegions(final Entity entity, MovementType movementType, Location newLocation, final Event parentEvent) {
		final WorldGuardUtils worldGuardUtils = new WorldGuardUtils(newLocation);

		Set<ProtectedRegion> regions = getRegions(entity);
		Set<ProtectedRegion> oldRegions = new HashSet<>(regions);

		Set<ProtectedRegion> applicableRegions = worldGuardUtils.getRegionsAt(newLocation);

		for (final ProtectedRegion region : applicableRegions) {
			if (!regions.contains(region)) {
				if (!RegionEventFactory.of(EnteringRegionEvent.class, region, entity, movementType, parentEvent).callEvent()) {
					regions.clear();
					regions.addAll(oldRegions);
				} else {
					Tasks.wait(1, () -> RegionEventFactory.of(EnteredRegionEvent.class, region, entity, movementType, parentEvent).callEvent());
					regions.add(region);
				}
			}
		}

		Iterator<ProtectedRegion> iterator = regions.iterator();
		while (iterator.hasNext()) {
			final ProtectedRegion region = iterator.next();
			if (!applicableRegions.contains(region)) {
				if (worldGuardUtils.getManager().getRegion(region.getId()) != region) {
					iterator.remove();
					continue;
				}

				if (!RegionEventFactory.of(LeavingRegionEvent.class, region, entity, movementType, parentEvent).callEvent()) {
					regions.clear();
					regions.addAll(oldRegions);
				} else {
					Tasks.wait(1, () -> RegionEventFactory.of(LeftRegionEvent.class, region, entity, movementType, parentEvent).callEvent());
					iterator.remove();
				}
			}
		}

		entities.put(entity, regions);
	}
}
