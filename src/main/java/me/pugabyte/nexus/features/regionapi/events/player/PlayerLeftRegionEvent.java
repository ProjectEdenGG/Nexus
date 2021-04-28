package me.pugabyte.nexus.features.regionapi.events.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.LeftRegionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a player left a WorldGuard region
 */
@Getter(AccessLevel.PUBLIC)
public class PlayerLeftRegionEvent extends LeftRegionEvent {
	protected final Player player;

	/**
	 * Creates a new PlayerLeftRegionEvent
	 *
	 * @param region       the region the player has left
	 * @param player       the player who triggered the event
	 * @param movementType the type of movement how the player left the region
	 */
	public PlayerLeftRegionEvent(ProtectedRegion region, Player player, MovementType movementType, Event parent) {
		super(region, player, movementType, parent);
		this.player = player;
	}

}
