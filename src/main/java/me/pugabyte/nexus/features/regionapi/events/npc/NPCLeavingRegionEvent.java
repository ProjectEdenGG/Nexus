package me.pugabyte.nexus.features.regionapi.events.npc;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.LeavingRegionEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a npc leaves a WorldGuard region, can be cancelled sometimes
 */
@Getter(AccessLevel.PUBLIC)
public class NPCLeavingRegionEvent extends LeavingRegionEvent {
	protected final NPC npc;

	/**
	 * creates a new NPCLeavingRegionEvent
	 *
	 * @param region       the region the npc is leaving
	 * @param npc          the npc who triggered this event
	 * @param movementType the type of movement how the npc leaves the region
	 * @param parentEvent  the event that triggered this event
	 */
	public NPCLeavingRegionEvent(ProtectedRegion region, NPC npc, MovementType movementType, Event parentEvent) {
		super(region, npc.getEntity(), movementType, parentEvent);
		this.npc = npc;
	}

}
