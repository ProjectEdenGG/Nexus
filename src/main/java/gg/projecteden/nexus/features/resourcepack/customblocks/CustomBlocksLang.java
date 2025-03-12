package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.entity.Player;

import java.util.List;

public class CustomBlocksLang {

	public static void debug(Player player, String message) {
		Debug.log(player, DebugType.CUSTOM_BLOCKS, message);
	}

	public static void debugLine(Player player) {
		debug(player, "");
	}

	public static void broadcastDebug(String message) {
		Debug.log(DebugType.CUSTOM_BLOCKS, message);
	}

	public static void janitorDebug(String message) {
		Debug.log(DebugType.CUSTOM_BLOCKS_JANITOR, message);
	}
}
