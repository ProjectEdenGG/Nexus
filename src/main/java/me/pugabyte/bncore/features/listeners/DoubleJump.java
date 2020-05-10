package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.concurrent.atomic.AtomicInteger;

//TODO is it possible to use paper's jumpEvent instead of toggle fly?
@Disabled
public class DoubleJump implements Listener {
	static final int COOLDOWN = 10 * 20;
	static final double VELOCITY = 1;

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if (!canDoubleJump(player)) return;

		event.setCancelled(true);
		player.setAllowFlight(false);
		player.setFlying(false);

		player.setVelocity(player.getLocation().getDirection().multiply(VELOCITY).setY(1));

		AtomicInteger repeat = new AtomicInteger(-1);
		repeat.set(Tasks.repeat(10, 2, () -> {
			if (player.isOnGround()) {
				player.setAllowFlight(true);
				Tasks.cancel(repeat.get());
			}
		}));
	}

	private boolean canDoubleJump(Player player) {
		if (player.getGameMode() == GameMode.CREATIVE)
			return false;

		try {
			new CooldownService().check(player, "doublejump", COOLDOWN);
		} catch (CooldownException ex) {
			return false;
		}

//		if (player.hasPermission("double.jump"))
//			return true;

		if (new WorldGuardUtils(player.getWorld()).getRegionsLikeAt(player.getLocation(), ".*doublejump.*").size() > 0)
			return true;

		return false;
	}
}
