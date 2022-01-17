package gg.projecteden.nexus.features.regionapi.events.common.abstraction;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a entity enters or leaves a WorldGuard region
 */
public abstract class NonCancellableRegionEvent extends RegionEvent {

	/**
	 * Creates a new NonCancellableRegionEvent
	 *
	 * @param region       the region the entity has left
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity left the region
	 * @param newLocation  the location the entity moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public NonCancellableRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, entity, movementType, newLocation, parentEvent);
	}

}
