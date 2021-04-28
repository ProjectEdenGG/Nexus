package me.pugabyte.nexus.features.regionapi.events.npc;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.LeftRegionEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Event;

/**
 * Event that is triggered after a npc left a WorldGuard region
 */
@Getter(AccessLevel.PUBLIC)
public class NPCLeftRegionEvent extends LeftRegionEvent {
	protected final NPC npc;

	/**
	 * Creates a new NPCLeftRegionEvent
	 *
	 * @param region       the region the npc has left
	 * @param npc          the npc who triggered the event
	 * @param movementType the type of movement how the npc left the region
	 */
	public NPCLeftRegionEvent(ProtectedRegion region, NPC npc, MovementType movementType, Event parent) {
		super(region, npc.getEntity(), movementType, parent);
		this.npc = npc;
	}

}
