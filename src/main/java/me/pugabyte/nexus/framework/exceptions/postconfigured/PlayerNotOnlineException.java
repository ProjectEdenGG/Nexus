package me.pugabyte.nexus.framework.exceptions.postconfigured;

import org.bukkit.OfflinePlayer;

public class PlayerNotOnlineException extends PostConfiguredException {

	public PlayerNotOnlineException(OfflinePlayer offlinePlayer) {
		super(offlinePlayer.getName() + " is not online");
	}

}
