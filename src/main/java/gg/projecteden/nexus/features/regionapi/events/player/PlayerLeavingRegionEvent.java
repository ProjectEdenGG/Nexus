package gg.projecteden.nexus.features.regionapi.events.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import gg.projecteden.nexus.features.regionapi.events.common.LeavingRegionEvent;
import gg.projecteden.parchment.HasPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a player leaves a WorldGuard region, can be cancelled sometimes
 */
@Getter(AccessLevel.PUBLIC)
public class PlayerLeavingRegionEvent extends LeavingRegionEvent implements HasPlayer {
	protected final Player player;

	/**
	 * creates a new PlayerLeavingRegionEvent
	 *
	 * @param region       the region the player is leaving
	 * @param player       the player who triggered this event
	 * @param movementType the type of movement how the player leaves the region
	 * @param newLocation  the location the player moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public PlayerLeavingRegionEvent(ProtectedRegion region, Player player, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, player, movementType, newLocation, parentEvent);
		this.player = player;
	}

}
