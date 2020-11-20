package me.pugabyte.nexus.models;

import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.utils.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class PlayerOwnedObject {

	public abstract UUID getUuid();

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(getUuid());
	}

	public Player getPlayer() {
		if (!getOfflinePlayer().isOnline())
			throw new PlayerNotOnlineException(getOfflinePlayer());
		return getOfflinePlayer().getPlayer();
	}

	public boolean isOnline() {
		return getOfflinePlayer().isOnline() && getOfflinePlayer().getPlayer() != null;
	}

	public void send(String message) {
		send(new JsonBuilder(message));
	}

	public void send(JsonBuilder message) {
		if (isOnline())
			getPlayer().sendMessage(message.build());
	}

}
