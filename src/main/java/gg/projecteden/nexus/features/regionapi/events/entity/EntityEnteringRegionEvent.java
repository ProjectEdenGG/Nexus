package gg.projecteden.nexus.features.regionapi.events.entity;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import gg.projecteden.nexus.features.regionapi.events.common.EnteringRegionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a entity enters a WorldGuard region, can be cancelled sometimes
 */
public class EntityEnteringRegionEvent extends EnteringRegionEvent {

	/**
	 * Creates a new EntityEnteringRegionEvent
	 *
	 * @param region       the region the entity is entering
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity enters the region
	 * @param newLocation  the location the entity moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public EntityEnteringRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, entity, movementType, newLocation, parentEvent);
	}

}
