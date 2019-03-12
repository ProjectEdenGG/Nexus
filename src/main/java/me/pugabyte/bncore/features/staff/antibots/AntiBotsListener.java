package me.pugabyte.bncore.features.staff.antibots;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.staff.antibots.models.DeniedEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

import static me.pugabyte.bncore.features.staff.antibots.AntiBotsCommand.*;

public class AntiBotsListener implements Listener {

	AntiBotsListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onRefresh(ServerListPingEvent event) {
		String ip = event.getAddress().toString().replaceAll("/", "");

		if (!getAllowed().contains(ip)) {
			addAllowed(ip);
		}
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		String ip = event.getAddress().toString().replaceAll("/", "");
		String uuid = event.getPlayer().getUniqueId().toString();
		String name = event.getPlayer().getName();

		if (!getAllowed().contains(ip)) {
			if (BNCore.antiBots.isEnabled()) {
				addDenied(new DeniedEntry(ip, uuid, name));
				BNCore.getInstance().getLogger().warning("Connection for player \"" + event.getPlayer().getName() + "\" denied");
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "§cPlease add our server to your server list to join");
			} else {
				BNCore.getInstance().getLogger().warning("Player has not pinged server before, possible bot; use /antibots on to enable bot prevention measures.");
			}
		}
	}

}
