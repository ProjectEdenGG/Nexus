package gg.projecteden.nexus.features.events.y2022.easter22;

import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.entity.Player;

public class Easter22 {
	public static final int TOTAL_EASTER_EGGS = 20;

	public static boolean isAtEasterIsland(Player player) {
		return new WorldGuardUtils(player).isInRegion(player, "event");
	}

}
