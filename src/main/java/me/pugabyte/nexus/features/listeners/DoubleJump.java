package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.framework.annotations.Disabled;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Disabled
public class DoubleJump implements Listener {
	static final int COOLDOWN = 0;
	static final double VELOCITY = 0.5;

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();

		if (!canDoubleJump(player)) {
			// Bear Fair Specific
			/*
			Location loc = player.getLocation();
			ProtectedRegion region = getWGUtils().getProtectedRegion(Basketball.courtRg);
			if (!getWGUtils().getRegionsAt(loc).contains(region)) return;
			event.setCancelled(true);
			player.setFlying(false);
			player.setAllowFlight(false);
			*/
			//
			return;
		}

		event.setCancelled(true);
		player.setAllowFlight(false);
		player.setFlying(false);

		player.setVelocity(player.getLocation().getDirection().multiply(VELOCITY).setY(1));

		AtomicInteger repeat = new AtomicInteger(-1);
		repeat.set(Tasks.repeat(10, 2, () -> {
			if (player.isOnGround()) {

				// Only enable fly if you're still in the double jump region
				/*
				Location loc = player.getLocation();
				ProtectedRegion region = getWGUtils().getProtectedRegion(Basketball.courtRg);
				if (getWGUtils().getRegionsAt(loc).contains(region))
					player.setAllowFlight(true);
				*/
				//

				Tasks.cancel(repeat.get());
			} else {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}));
	}

	private boolean canDoubleJump(Player player) {
		if (player.getGameMode() == GameMode.CREATIVE)
			return false;

		if (!new CooldownService().check(player, "doublejump", COOLDOWN))
			return false;

		if (!player.getAllowFlight())
			return false;

//		if (player.hasPermission("double.jump"))
//			return true;

		if (new WorldGuardUtils(player).getRegionsLikeAt(".*doublejump.*", player.getLocation()).size() > 0)
			return true;

		return false;
	}
}
