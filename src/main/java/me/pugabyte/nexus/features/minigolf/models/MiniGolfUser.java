package me.pugabyte.nexus.features.minigolf.models;

import lombok.Data;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class MiniGolfUser {
	private UUID uuid;
	private boolean debug;

	public void debug(boolean bool, String debug) {
		if (bool)
			debug(debug);
	}

	public void debug(String message) {
		if (debug)
			sendMessage(message);
	}

	public void sendMessage(String message) {
		getPlayer().sendMessage(message);
	}

	public OfflinePlayer getOfflinePlayer() {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid).getPlayer();
		if (offlinePlayer != null && offlinePlayer.getPlayer() != null)
			return offlinePlayer;
		return null;
	}

	public boolean isOnline() {
		return getOfflinePlayer().isOnline();
	}

	public Player getPlayer() {
		OfflinePlayer offlinePlayer = getOfflinePlayer();
		if (offlinePlayer != null && offlinePlayer.isOnline())
			return offlinePlayer.getPlayer();
		return null;
	}
}
