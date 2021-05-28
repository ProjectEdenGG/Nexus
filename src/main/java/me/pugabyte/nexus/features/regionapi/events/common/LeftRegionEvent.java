package me.pugabyte.nexus.features.regionapi.events.common;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.abstraction.RegionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a entity left a WorldGuard region
 */
public class LeftRegionEvent extends RegionEvent {

	/**
	 * Creates a new EntityLeftRegionEvent
	 *
	 * @param region       the region the entity has left
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity left the region
	 * @param parentEvent  the event that triggered this event
	 */
	public LeftRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, entity, movementType, newLocation, parentEvent);
	}

}
