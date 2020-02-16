package me.pugabyte.bncore.features.afk;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import me.pugabyte.bncore.models.afk.AFKService;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class AFK {
	static Map<Player, AFKPlayer> players = new AFKService().getAll();

	public AFK() {
		new AFKListener();
		scheduler();
	}

	public static void shutdown() {
		new AFKService().saveAll();
	}

	private void scheduler() {
		Tasks.repeat(5 * 20, 3 * 20, () -> Bukkit.getOnlinePlayers().stream().map(AFK::get).forEach(player -> {
			try {
				if (!isSameLocation(player.getLocation(), player.getPlayer().getLocation()))
					if (player.isAfk() && !player.isForceAfk())
						player.notAfk();
					else
						player.update();
				else if (!player.isAfk() && player.isTimeAfk())
					player.afk();
			} catch (Exception ex) {
				BNCore.warn("Error in AFK scheduler: " + ex.getMessage());
			}
		}));
	}

	private boolean isSameLocation(Location from, Location to) {
		if (!from.getWorld().equals(to.getWorld()))
			return false;

		boolean x = (int) from.getX() == (int) to.getX();
		boolean y = (int) from.getY() == (int) to.getY();
		boolean z = (int) from.getZ() == (int) to.getZ();
		return x && y && z;
	}

	public static AFKPlayer get(Player player) {
		if (!players.containsKey(player))
			players.put(player, new AFKPlayer(player));

		return players.get(player);
	}

	public static int getActivePlayers() {
		if (Bukkit.getOnlinePlayers().size() == 0)
			return 0;
		else {
			int result = 0;
			for (Player player : Bukkit.getOnlinePlayers())
				if (!get(player).isAfk())
					++result;

			return result;
		}
	}

}
