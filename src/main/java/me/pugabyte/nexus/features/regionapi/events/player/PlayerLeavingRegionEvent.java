package me.pugabyte.nexus.features.regionapi.events.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.LeavingRegionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a player leaves a WorldGuard region, can be cancelled sometimes
 */
@Getter(AccessLevel.PUBLIC)
public class PlayerLeavingRegionEvent extends LeavingRegionEvent {
	protected final Player player;

	/**
	 * creates a new PlayerLeavingRegionEvent
	 *
	 * @param region       the region the player is leaving
	 * @param player       the player who triggered this event
	 * @param movementType the type of movement how the player leaves the region
	 * @param parentEvent  the event that triggered this event
	 */
	public PlayerLeavingRegionEvent(ProtectedRegion region, Player player, MovementType movementType, Event parentEvent) {
		super(region, player, movementType, parentEvent);
		this.player = player;
	}

}
