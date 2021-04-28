package me.pugabyte.nexus.features.regionapi.events.common.abstraction;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
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
	 * @param parentEvent  the event that triggered this event
	 */
	public NonCancellableRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parentEvent) {
		super(region, entity, movementType, parentEvent);
	}

}
