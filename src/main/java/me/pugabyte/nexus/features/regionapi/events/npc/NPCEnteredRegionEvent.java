package me.pugabyte.nexus.features.regionapi.events.npc;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.EnteredRegionEvent;
import net.citizensnpcs.api.npc.NPC;
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
	 * @param npc          the npc who triggered the event
	 * @param movementType the type of movement how the npc entered the region
	 */
	public NPCEnteredRegionEvent(ProtectedRegion region, NPC npc, MovementType movementType, Event parent) {
		super(region, npc.getEntity(), movementType, parent);
		this.npc = npc;
	}

}
