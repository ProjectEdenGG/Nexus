package me.pugabyte.nexus.features.regionapi.events.entity;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.LeavingRegionEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a entity leaves a WorldGuard region, can be cancelled sometimes
 */
public class EntityLeavingRegionEvent extends LeavingRegionEvent {

	/**
	 * creates a new EntityLeavingRegionEvent
	 *
	 * @param region       the region the entity is leaving
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity leaves the region
	 * @param parentEvent  the event that triggered this event
	 */
	public EntityLeavingRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parentEvent) {
		super(region, entity, movementType, parentEvent);
	}

}
