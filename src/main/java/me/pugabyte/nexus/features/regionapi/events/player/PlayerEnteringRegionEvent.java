package me.pugabyte.nexus.features.regionapi.events.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.EnteringRegionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a player enters a WorldGuard region, can be cancelled sometimes
 */
@Getter(AccessLevel.PUBLIC)
public class PlayerEnteringRegionEvent extends EnteringRegionEvent {
	protected final Player player;

	/**
	 * Creates a new PlayerEnteringRegionEvent
	 *
	 * @param region       the region the player is entering
	 * @param player       the player who triggered the event
	 * @param movementType the type of movement how the player enters the region
	 */
	public PlayerEnteringRegionEvent(ProtectedRegion region, Player player, MovementType movementType, Event parent) {
		super(region, player, movementType, parent);
		this.player = player;
	}

}
