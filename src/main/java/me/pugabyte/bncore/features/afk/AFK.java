package me.pugabyte.bncore.features.afk;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AFK {
	static HashMap<Player, AFKPlayer> players = new HashMap<>();

	public AFK() {
		new AFKListener();
		scheduler();
	}

	private void scheduler() {
		Utils.repeat(5 * 20, 3 * 20, () -> Bukkit.getOnlinePlayers().stream().map(AFK::get).forEach(player -> {
			try {
				if (!player.getLocation().equals(player.getPlayer().getLocation()))
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

	public static AFKPlayer get(Player player) {
		if (!players.containsKey(player))
			players.put(player, new AFKPlayer(player));

		return players.get(player);
	}

}
