package me.pugabyte.bncore.framework.commands;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static me.pugabyte.bncore.utils.StringUtils.right;

@NoArgsConstructor
public class CommandListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		for (String redirect : Commands.getRedirects().keySet()) {
			if (!event.getMessage().toLowerCase().startsWith(redirect.toLowerCase())) continue;

			event.setCancelled(true);
			String to = Commands.getRedirects().get(event.getMessage());
			Bukkit.dispatchCommand(event.getPlayer(), right(to, to.length() - 1));
			return;
		}
	}
}
