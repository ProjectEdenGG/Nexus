package me.pugabyte.nexus.features.regionapi.events.common;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.abstraction.CancellableRegionEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a entity leaves a WorldGuard region, can be cancelled sometimes
 */
public class LeavingRegionEvent extends CancellableRegionEvent {

	/**
	 * creates a new EntityLeavingRegionEvent
	 *
	 * @param region       the region the entity is leaving
	 * @param entity       the entity who triggered the event
	 * @param movementType the type of movement how the entity leaves the region
	 */
	public LeavingRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parent) {
		super(region, entity, movementType, parent);
	}

}
