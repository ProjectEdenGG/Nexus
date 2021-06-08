package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.utils.Name;
import org.bukkit.OfflinePlayer;

public class PlayerNotOnlineException extends PostConfiguredException {

	public PlayerNotOnlineException(OfflinePlayer offlinePlayer) {
		super(Name.of(offlinePlayer) + " is not online");
	}

}
