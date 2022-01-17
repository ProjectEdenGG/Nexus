package gg.projecteden.nexus.features.listeners;

import gg.projecteden.parchment.HasPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface TemporaryListener extends Listener, HasPlayer {

	Player getPlayer();

	default void unregister() {}

}
