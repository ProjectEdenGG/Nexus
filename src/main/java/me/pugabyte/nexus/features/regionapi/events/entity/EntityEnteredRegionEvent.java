package me.pugabyte.nexus.features.regionapi.events.entity;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.EnteredRegionEvent;
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
	 * @param entity       the entity who triggered the event
	 * @param movementType the type of movement how the entity entered the region
	 */
	public EntityEnteredRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parent) {
		super(region, entity, movementType, parent);
	}

}
