package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CustomContentUtils {

	public static boolean hasBypass(Player player) {
		if (Nexus.getEnv() == Env.TEST)
			return true;

		WorldGroup worldGroup = WorldGroup.of(player);
		if (worldGroup == WorldGroup.STAFF || worldGroup == WorldGroup.CREATIVE)
			return true;

		Rank rank = Rank.of(player);
		if (rank.isStaff()) {
			if (player.getGameMode().equals(GameMode.CREATIVE))
				return true;

			EdenEvent edenEvent = EdenEvent.of(player);
			if (edenEvent != null) {
				if (!edenEvent.isEventActive())
					return true;
			}
		}

		return false;
	}
}
