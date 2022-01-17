package gg.projecteden.nexus.features.events.aeveonproject.effects;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import static gg.projecteden.nexus.features.events.aeveonproject.AeveonProject.getWorld;

public class PlayerTime implements Listener {

	public PlayerTime() {
		Nexus.registerListener(this);

		Tasks.repeat(0, TickTime.TICK.x(10), () -> {
			for (Player player : OnlinePlayers.getAll()) {
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
