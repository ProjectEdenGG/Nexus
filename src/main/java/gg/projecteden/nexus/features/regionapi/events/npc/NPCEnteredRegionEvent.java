package gg.projecteden.nexus.features.regionapi.events.npc;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import gg.projecteden.nexus.features.regionapi.events.common.EnteredRegionEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a npc entered a WorldGuard region
 */
@Getter(AccessLevel.PUBLIC)
public class NPCEnteredRegionEvent extends EnteredRegionEvent {
	protected final NPC npc;

	/**
	 * creates a new NPCEnteredRegionEvent
	 *
	 * @param region       the region the npc entered
	 * @param npc          the npc who triggered this event
	 * @param movementType the type of movement how the npc entered the region
	 * @param newLocation  the location the npc moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public NPCEnteredRegionEvent(ProtectedRegion region, NPC npc, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, npc.getEntity(), movementType, newLocation, parentEvent);
		this.npc = npc;
	}

}
