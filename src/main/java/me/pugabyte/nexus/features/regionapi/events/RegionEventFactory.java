package me.pugabyte.nexus.features.regionapi.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.EnteredRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.common.EnteringRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.common.LeavingRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.common.LeftRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.common.abstraction.RegionEvent;
import me.pugabyte.nexus.features.regionapi.events.entity.EntityEnteredRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.entity.EntityEnteringRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.entity.EntityLeavingRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.entity.EntityLeftRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.npc.NPCEnteredRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.npc.NPCEnteringRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.npc.NPCLeavingRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.npc.NPCLeftRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import me.pugabyte.nexus.utils.CitizensUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class RegionEventFactory {

	/**
	 * Returns the correct event based on the entity provided
	 */
	public static RegionEvent of(Class<? extends RegionEvent> eventClass, ProtectedRegion region, Entity entity, MovementType movementType, Location newLocation, Event parentEvent) {
		if (CitizensUtils.isNPC(entity)) {
			NPC npc = CitizensUtils.getNPC(entity);
			if (eventClass == EnteredRegionEvent.class)
				return new NPCEnteredRegionEvent(region, npc, movementType, newLocation, parentEvent);
			else if (eventClass == EnteringRegionEvent.class)
				return new NPCEnteringRegionEvent(region, npc, movementType, newLocation, parentEvent);
			else if (eventClass == LeavingRegionEvent.class)
				return new NPCLeavingRegionEvent(region, npc, movementType, newLocation, parentEvent);
			else if (eventClass == LeftRegionEvent.class)
				return new NPCLeftRegionEvent(region, npc, movementType, newLocation, parentEvent);
		} else if (entity instanceof Player player) {
			if (eventClass == EnteredRegionEvent.class)
				return new PlayerEnteredRegionEvent(region, player, movementType, newLocation, parentEvent);
			else if (eventClass == EnteringRegionEvent.class)
				return new PlayerEnteringRegionEvent(region, player, movementType, newLocation, parentEvent);
			else if (eventClass == LeavingRegionEvent.class)
				return new PlayerLeavingRegionEvent(region, player, movementType, newLocation, parentEvent);
			else if (eventClass == LeftRegionEvent.class)
				return new PlayerLeftRegionEvent(region, player, movementType, newLocation, parentEvent);
		} else {
			if (eventClass == EnteredRegionEvent.class)
				return new EntityEnteredRegionEvent(region, entity, movementType, newLocation, parentEvent);
			else if (eventClass == EnteringRegionEvent.class)
				return new EntityEnteringRegionEvent(region, entity, movementType, newLocation, parentEvent);
			else if (eventClass == LeavingRegionEvent.class)
				return new EntityLeavingRegionEvent(region, entity, movementType, newLocation, parentEvent);
			else if (eventClass == LeftRegionEvent.class)
				return new EntityLeftRegionEvent(region, entity, movementType, newLocation, parentEvent);
		}

		throw new IllegalArgumentException("No region event found for class " + eventClass.getSimpleName() + " and entity " + entity.getClass().getSimpleName());
	}

}
