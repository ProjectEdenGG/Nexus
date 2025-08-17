package gg.projecteden.nexus.features.bigdoors;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class Listeners implements Listener {
	public Listeners() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		if (!event.getRegion().getId().matches(".*_bigdoor_\\d+_hitbox"))
			return;

		Player player = event.getPlayer();
		if (PlayerUtils.isHidden(player))
			return;

		if (CooldownService.isOnCooldown(event.getPlayer(), "bigdoor_knockback", TickTime.SECOND))
			return;

		Location playerLoc = player.getLocation().clone();
		Location doorLoc = event.getNewLocation().clone();

		if (event.getNewLocation().getNearbyEntitiesByType(FallingBlock.class, 1, 2, 1).size() > 0) {
			event.setCancelled(true);

			doorLoc.setDirection(playerLoc.getDirection());

			Vector away = playerLoc.toVector().add(doorLoc.toVector().multiply(-1)).multiply(2);
			away.setY(0.2);

			Tasks.wait(1, () -> {
				player.setVelocity(away);

				new SoundBuilder(CustomSound.BONK)
					.location(player.getLocation())
					.receiver(player)
					.muteMenuItem(MuteMenuItem.JOKES)
					.play();
			});
		}
	}
}
