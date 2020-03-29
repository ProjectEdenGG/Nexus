package me.pugabyte.bncore.framework.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static me.pugabyte.bncore.utils.StringUtils.noSlash;

@NoArgsConstructor
public class CommandListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		BNCore.log("Command run by " + event.getPlayer().getName() + ": " + event.getMessage());
		for (String redirect : Commands.getRedirects().keySet()) {
			if (!event.getMessage().toLowerCase().startsWith(redirect.toLowerCase())) continue;

			event.setCancelled(true);
			String to = Commands.getRedirects().get(event.getMessage());
			Utils.runCommand(event.getPlayer(), noSlash(to));
			return;
		}
	}

}
