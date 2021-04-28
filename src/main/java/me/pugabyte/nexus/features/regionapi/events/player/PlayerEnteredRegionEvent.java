package me.pugabyte.nexus.features.regionapi.events.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.EnteredRegionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a player entered a WorldGuard region
 */
@Getter(AccessLevel.PUBLIC)
public class PlayerEnteredRegionEvent extends EnteredRegionEvent {
	protected final Player player;

	/**
	 * creates a new PlayerEnteredRegionEvent
	 *
	 * @param region       the region the player entered
	 * @param player       the player who triggered this event
	 * @param movementType the type of movement how the player entered the region
	 * @param parentEvent  the event that triggered this event
	 */
	public PlayerEnteredRegionEvent(ProtectedRegion region, Player player, MovementType movementType, Event parentEvent) {
		super(region, player, movementType, parentEvent);
		this.player = player;
	}

}
