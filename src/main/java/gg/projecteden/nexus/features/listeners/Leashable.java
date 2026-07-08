package gg.projecteden.nexus.features.listeners;

import gg.projecteden.parchment.event.entity.CheckEntityLeashableEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Leashable implements Listener {

	List<EntityType> buggedTypes = List.of(EntityType.BAT, EntityType.GHAST);
	List<EntityType> bossTypes = List.of(EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.WARDEN, EntityType.ELDER_GUARDIAN);
	List<EntityType> ignoreTypes = new ArrayList<>() {{
		addAll(buggedTypes);
		addAll(bossTypes);
		addAll(List.of(EntityType.PLAYER, EntityType.NPC));
	}};

	@EventHandler
	public void on(CheckEntityLeashableEvent event) {
		event.setLeashable(!ignoreTypes.contains(event.getEntity().getType()));
	}


}
