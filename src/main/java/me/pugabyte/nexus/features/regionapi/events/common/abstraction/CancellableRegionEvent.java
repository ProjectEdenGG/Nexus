package me.pugabyte.nexus.features.regionapi.events.common.abstraction;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a entity enters a WorldGuard region, can be cancelled sometimes
 */
public abstract class CancellableRegionEvent extends RegionEvent implements Cancellable {
	protected boolean cancelled;

	/**
	 * Creates a new CancellableRegionEvent
	 *
	 * @param region       the region the entity is entering
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity enters the region
	 * @param newLocation  the location the entity moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public CancellableRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, entity, movementType, newLocation, parentEvent);
		cancelled = false;
	}

	/**
	 * Retrieves whether this event will be cancelled/has been cancelled by any plugin
	 *
	 * @return true if this event will be cancelled and the entity will be stopped from moving
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	/**
	 * Sets whether this event should be cancelled
	 * When the event is cancelled, the entity will not be able to move into the region
	 *
	 * @param cancelled true if the entity should be stopped from moving into the region
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		if (isCancellable())
			((Cancellable) parentEvent).setCancelled(cancelled);
	}

	/**
	 * Sometimes you can not cancel an event, i.e. disconnects
	 *
	 * @return whether you can cancel this event
	 */
	public boolean isCancellable() {
		return parentEvent instanceof Cancellable;
	}

}

