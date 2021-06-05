package me.pugabyte.nexus.features.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface TemporaryListener extends Listener {

	Player getPlayer();

	default void unregister() {}

}
