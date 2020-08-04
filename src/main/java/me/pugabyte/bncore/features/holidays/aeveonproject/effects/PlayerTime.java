package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Collection;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.*;

public class PlayerTime implements Listener {

	public PlayerTime() {
		BNCore.registerListener(this);

		Tasks.repeat(0, Time.TICK.x(10), () -> {
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			for (Player player : players) {
				if (!isInWorld(player)) continue;

				if (isInSpace(player)) {
					if (player.getPlayerTime() != 570000)
						player.setPlayerTime(18000, false);
				} else {
					if (player.getPlayerTime() == 570000)
						player.resetPlayerTime();
				}
			}
		});
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		if (event.getFrom().equals(WORLD))
			event.getPlayer().resetPlayerTime();
	}
}
