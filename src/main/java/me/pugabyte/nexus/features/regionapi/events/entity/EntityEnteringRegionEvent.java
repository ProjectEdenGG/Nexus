package me.pugabyte.nexus.features.regionapi.events.entity;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.EnteringRegionEvent;
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
	 * @param entity       the entity who triggered the event
	 * @param movementType the type of movement how the entity enters the region
	 */
	public EntityEnteringRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parent) {
		super(region, entity, movementType, parent);
	}

}
