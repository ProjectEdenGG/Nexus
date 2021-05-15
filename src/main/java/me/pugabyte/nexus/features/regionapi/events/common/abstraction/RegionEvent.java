package me.pugabyte.nexus.features.regionapi.events.common.abstraction;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

@Getter(AccessLevel.PUBLIC)
public abstract class RegionEvent extends EntityEvent {
	protected final ProtectedRegion region;
	protected final MovementType movementType;
	protected final Event parentEvent;

	/**
	 * Creates a new RegionEvent
	 *
	 * @param region       the region the entity has left
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity left the region
	 * @param parentEvent  the event that triggered this event
	 */
	public RegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parentEvent) {
		super(entity);
		this.region = region;
		this.movementType = movementType;
		this.parentEvent = parentEvent;
	}

	private static final HandlerList handlerList = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlerList;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
