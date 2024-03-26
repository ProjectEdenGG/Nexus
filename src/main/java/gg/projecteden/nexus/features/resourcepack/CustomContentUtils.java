package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CustomContentUtils {

	public static boolean hasBypass(Player player) {

		if (Rank.of(player).isAdmin() && player.getGameMode().equals(GameMode.CREATIVE)) {
			return true;
		}

		WorldGroup worldGroup = WorldGroup.of(player);
		if (worldGroup == WorldGroup.STAFF || worldGroup == WorldGroup.CREATIVE)
			return true;

		return false;

	}
}
