package gg.projecteden.nexus.framework.exceptions.postconfigured;

import gg.projecteden.nexus.utils.Name;
import org.bukkit.OfflinePlayer;

public class PlayerNotOnlineException extends PostConfiguredException {

	public PlayerNotOnlineException(OfflinePlayer offlinePlayer) {
		super(Name.of(offlinePlayer) + " is not online");
	}

}
