package gg.projecteden.nexus.features.regionapi.events.entity;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import gg.projecteden.nexus.features.regionapi.events.common.EnteredRegionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a entity entered a WorldGuard region
 */
public class EntityEnteredRegionEvent extends EnteredRegionEvent {

	/**
	 * creates a new EntityEnteredRegionEvent
	 *
	 * @param region       the region the entity entered
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity entered the region
	 * @param newLocation  the location the entity moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public EntityEnteredRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, entity, movementType, newLocation, parentEvent);
	}

}
