package me.pugabyte.nexus.features.regionapi;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
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
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static eden.utils.Utils.isNullOrEmpty;

@NoArgsConstructor
public class WorldGuardRegionAPI extends Feature implements Listener {
	private static final Map<UUID, Set<ProtectedRegion>> entityRegions = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		updateRegions(event.getPlayer(), MovementType.CONNECT, event.getPlayer().getLocation(), event);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		clearRegions(event.getPlayer(), MovementType.DISCONNECT, event);
	}

	@EventHandler
	public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
		clearRegions(event.getEntity(), MovementType.DESPAWN, event);
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
	public void onVehicleEnter(VehicleEnterEvent event) {
		updateRegions(event.getEntered(), MovementType.ENTER_VEHICLE, event.getVehicle().getLocation(), event);
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		updateRegions(event.getPlayer(), MovementType.ENTER_BED, event.getBed().getLocation(), event);
	}

	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent event) {
		updateRegions(event.getEntity(), MovementType.TELEPORT, event.getTo(), event);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		updateRegions(event.getPlayer(), MovementType.MOVE, event.getTo(), event);
	}

	@EventHandler
	public void onEntityMove(EntityMoveEvent event) {
		// TODO Config option? Taking up quite a bit of tick runtime
		if (event.getTo().getWorld().getName().equals("bearfair21"))
			updateRegions(event.getEntity(), MovementType.MOVE, event.getTo(), event);
	}

	@EventHandler
	public void onVehicleMove(VehicleMoveEvent event) {
		if (event.getVehicle().getPassengers().isEmpty())
			return;

		for (Entity entity : event.getVehicle().getPassengers())
			if (updateRegions(entity, MovementType.RIDE, event.getTo(), event)) {
				event.getVehicle().removePassenger(entity);
				entity.teleportAsync(event.getFrom());
			}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		updateRegions(event.getPlayer(), MovementType.RESPAWN, event.getRespawnLocation(), event);
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		updateRegions(event.getEntity(), MovementType.SPAWN, event.getLocation(), event);
	}

	private void clearRegions(Entity entity, MovementType movementType, Event event) {
		Set<ProtectedRegion> regions = entityRegions.remove(entity.getUniqueId());
		if (isNullOrEmpty(regions))
			return;

		for (ProtectedRegion region : regions) {
			RegionEventFactory.of(LeavingRegionEvent.class, region, entity, movementType, event).callEvent();
			RegionEventFactory.of(LeftRegionEvent.class, region, entity, movementType, event).callEvent();
		}
	}

	private synchronized boolean updateRegions(final Entity entity, MovementType movementType, Location newLocation, final Event parentEvent) {
		final WorldGuardUtils worldGuardUtils = new WorldGuardUtils(newLocation);

		Set<ProtectedRegion> regions = entityRegions.getOrDefault(entity.getUniqueId(), new HashSet<>());
		regions.removeIf(Objects::isNull);

		Set<ProtectedRegion> originalRegions = new HashSet<>(regions);
		Set<ProtectedRegion> applicableRegions = worldGuardUtils.getRegionsAt(newLocation);

		for (final ProtectedRegion region : applicableRegions)
			if (!originalRegions.contains(region))
				if (!RegionEventFactory.of(EnteringRegionEvent.class, region, entity, movementType, parentEvent).callEvent()) {
					regions.clear();
					regions.addAll(originalRegions);
					return true;
				} else {
					Tasks.wait(1, () -> RegionEventFactory.of(EnteredRegionEvent.class, region, entity, movementType, parentEvent).callEvent());
					regions.add(region);
				}

		for (final ProtectedRegion region : originalRegions)
			if (!applicableRegions.contains(region))
				if (!RegionEventFactory.of(LeavingRegionEvent.class, region, entity, movementType, parentEvent).callEvent()) {
					regions.clear();
					regions.addAll(originalRegions);
					return true;
				} else {
					Tasks.wait(1, () -> RegionEventFactory.of(LeftRegionEvent.class, region, entity, movementType, parentEvent).callEvent());
					regions.remove(region);
				}

		entityRegions.put(entity.getUniqueId(), regions);
		return false;
	}
}
