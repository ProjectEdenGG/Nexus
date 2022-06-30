package gg.projecteden.nexus.features.regionapi.events.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import gg.projecteden.nexus.features.regionapi.events.common.LeftRegionEvent;
import gg.projecteden.parchment.HasPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a player left a WorldGuard region
 */
@Getter(AccessLevel.PUBLIC)
public class PlayerLeftRegionEvent extends LeftRegionEvent implements HasPlayer {
	protected final Player player;

	/**
	 * Creates a new PlayerLeftRegionEvent
	 *
	 * @param region       the region the player has left
	 * @param player       the player who triggered this event
	 * @param movementType the type of movement how the player left the region
	 * @param newLocation  the location the player moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public PlayerLeftRegionEvent(ProtectedRegion region, Player player, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, player, movementType, newLocation, parentEvent);
		this.player = player;
	}

}
