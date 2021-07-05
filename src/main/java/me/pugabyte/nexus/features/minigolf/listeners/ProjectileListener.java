package me.pugabyte.nexus.features.minigolf.listeners;

import me.pugabyte.nexus.Nexus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileListener implements Listener {

	public ProjectileListener() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {

	}
}
