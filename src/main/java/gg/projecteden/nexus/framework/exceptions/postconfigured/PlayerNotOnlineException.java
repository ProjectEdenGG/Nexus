package gg.projecteden.nexus.framework.exceptions.postconfigured;

import gg.projecteden.nexus.utils.Name;
import me.lexikiq.HasUniqueId;

import java.util.UUID;

public class PlayerNotOnlineException extends PostConfiguredException {

	public PlayerNotOnlineException(UUID playerUUID) {
		super(Name.of(playerUUID) + " is not online");
	}

	public PlayerNotOnlineException(HasUniqueId offlinePlayer) {
		super(Name.of(offlinePlayer) + " is not online");
	}

}
