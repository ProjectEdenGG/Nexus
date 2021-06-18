package me.pugabyte.nexus.features.events.aeveonproject.effects;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.aeveonproject.APUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import static me.pugabyte.nexus.features.events.aeveonproject.AeveonProject.getWorld;

public class PlayerTime implements Listener {

	public PlayerTime() {
		Nexus.registerListener(this);

		Tasks.repeat(0, Time.TICK.x(10), () -> {
			for (Player player : PlayerUtils.getOnlinePlayers()) {
				if (!APUtils.isInWorld(player)) continue;

				if (APUtils.isInSpace(player)) {
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
		if (event.getFrom().equals(getWorld()))
			event.getPlayer().resetPlayerTime();
	}
}
