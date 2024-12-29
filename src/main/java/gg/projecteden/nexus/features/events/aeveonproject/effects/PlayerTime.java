package gg.projecteden.nexus.features.events.aeveonproject.effects;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

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
		if (event.getFrom().equals(AeveonProject.getWorld()))
			event.getPlayer().resetPlayerTime();
	}
}
