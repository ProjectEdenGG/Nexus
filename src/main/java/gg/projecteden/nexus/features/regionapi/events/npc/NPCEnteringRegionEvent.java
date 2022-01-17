package gg.projecteden.nexus.features.regionapi.events.npc;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import gg.projecteden.nexus.features.regionapi.events.common.EnteringRegionEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a npc enters a WorldGuard region, can be cancelled sometimes
 */
@Getter(AccessLevel.PUBLIC)
public class NPCEnteringRegionEvent extends EnteringRegionEvent {
	protected final NPC npc;

	/**
	 * Creates a new NPCEnteringRegionEvent
	 *
	 * @param region       the region the npc is entering
	 * @param npc          the npc who triggered this event
	 * @param movementType the type of movement how the npc enters the region
	 * @param newLocation  the location the npc moved to
	 * @param parentEvent  the event that triggered this event
	 */
	public NPCEnteringRegionEvent(ProtectedRegion region, NPC npc, MovementType movementType, Location newLocation, Event parentEvent) {
		super(region, npc.getEntity(), movementType, newLocation, parentEvent);
		this.npc = npc;
	}

}
