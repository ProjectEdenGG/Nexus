package gg.projecteden.nexus.framework.exceptions.postconfigured;

import gg.projecteden.nexus.utils.Name;
import me.lexikiq.HasOfflinePlayer;

import java.util.UUID;

public class PlayerNotOnlineException extends PostConfiguredException {

	public PlayerNotOnlineException(UUID playerUUID) {
		super(Name.of(playerUUID) + " is not online");
	}

	// TODO: replace with HasUniqueId constructor
	/**
	 * @deprecated Getting offline users involves I/O operations, please use UUID constructor
	 * for non-{@link org.bukkit.entity.Player Player} objects.
	 */
	@Deprecated
	public PlayerNotOnlineException(HasOfflinePlayer offlinePlayer) {
		super(Name.of(offlinePlayer) + " is not online");
	}

}
