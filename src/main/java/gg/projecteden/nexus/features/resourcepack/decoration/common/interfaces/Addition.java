package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import org.bukkit.entity.Player;

public interface Addition {

	String getPlacementError();

	default void placementError(Player player) {
		if (!DecorationCooldown.IMPROPER_PLACEMENT.isOnCooldown(player)) {
			DecorationError.IMPROPER_PLACEMENT.sendCustom(player, getPlacementError());
		}
	}
}
