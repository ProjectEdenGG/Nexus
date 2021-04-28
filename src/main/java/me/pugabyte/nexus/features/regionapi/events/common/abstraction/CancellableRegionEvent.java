package me.pugabyte.nexus.features.regionapi.events.common.abstraction;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a entity enters a WorldGuard region, can be cancelled sometimes
 */
public abstract class CancellableRegionEvent extends RegionEvent implements Cancellable {
	protected boolean cancelled;
	protected final boolean cancellable;

	/**
	 * Creates a new RegionEnterEvent
	 *
	 * @param region       the region the entity is entering
	 * @param entity       the entity who triggered the event
	 * @param movementType the type of movement how the entity enters the region
	 */
	public CancellableRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parent) {
		super(region, entity, movementType, parent);
		cancelled = false;
		cancellable = movementType.isCancellable();
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
		if (this.cancellable)
			this.cancelled = cancelled;
	}

	/**
	 * Sometimes you can not cancel an event, i.e. disconnects
	 *
	 * @return whether you can cancel this event
	 */
	public boolean isCancellable() {
		return this.cancellable;
	}

}

