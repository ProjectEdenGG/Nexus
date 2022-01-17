package gg.projecteden.nexus.framework.exceptions.postconfigured;

import gg.projecteden.interfaces.HasUniqueId;
import gg.projecteden.nexus.utils.Name;

import java.util.UUID;

public class PlayerNotOnlineException extends PostConfiguredException {

	public PlayerNotOnlineException(UUID uuid) {
		super(Name.of(uuid) + " is not online");
	}

	public PlayerNotOnlineException(HasUniqueId player) {
		super(Name.of(player) + " is not online");
	}

}
