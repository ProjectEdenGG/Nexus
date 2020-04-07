package me.pugabyte.bncore.framework.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map.Entry;

import static me.pugabyte.bncore.utils.StringUtils.trimFirst;

@NoArgsConstructor
public class CommandListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		for (Entry<String, String> redirect : Commands.getRedirects().entrySet()) {
			if (!event.getMessage().toLowerCase().startsWith(redirect.getKey()))
				continue;

			event.setCancelled(true);
			String command = redirect.getValue() + event.getMessage().substring(redirect.getKey().length());
			Utils.runCommand(event.getPlayer(), trimFirst(command));
			return;
		}
	}

}
