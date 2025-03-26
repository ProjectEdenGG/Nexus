package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CoreProtectUtils {

	private static final CoreProtectAPI coreProtectAPI = Nexus.getCoreProtectAPI();

	// PLACEMENT
	public static void logPlacement(Player player, Block block) {
		logPlacement(player.getName(), block);
	}

	public static void logPlacement(String source, Block block) {
		coreProtectAPI.logPlacement(source, block.getLocation(), block.getType(), block.getBlockData());
	}

	// REMOVAL
	public static void logRemoval(Player player, Block block) {
		logRemoval(player.getName(), block);
	}

	public static void logRemoval(String source, Block block) {
		coreProtectAPI.logRemoval(source, block.getLocation(), block.getType(), block.getBlockData());
	}
}
