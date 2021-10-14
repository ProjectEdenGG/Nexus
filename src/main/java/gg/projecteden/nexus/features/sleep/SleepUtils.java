package gg.projecteden.nexus.features.sleep;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class SleepUtils {

	public static boolean canSleep(Player player) {
		return !PlayerUtils.isVanished(player) && !AFK.get(player).isTimeAfk() && player.getGameMode() == GameMode.SURVIVAL;
	}

	public static List<Player> getCanSleep(World world) {
		return OnlinePlayers.where().world(world).get().stream().filter(SleepUtils::canSleep).toList();
	}

	public static List<Player> getSleeping(World world) {
		return getCanSleep(world).stream().filter(Player::isSleeping).toList();
	}

}
