package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.noSlash;

public class CommandOverrideListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		List<String> args = Arrays.asList(event.getMessage().split(" "));
		if (args.size() == 0) return;
		String argsString = event.getMessage().replace(args.get(0) + " ", "");

		Consumer<String> redirect = command -> Utils.runCommand(player, noSlash(command));
		Consumer<String> send = message -> player.sendMessage(colorize(message));

		switch (args.get(0)) {
			case "/fill":
				if (!player.hasPermission("minecraft.command.fill")) {
					event.setCancelled(true);
					redirect.accept("//fill " + argsString);
				}
				break;
			case "/restart":
			case "/bukkit:restart":
			case "/reload":
			case "/bukkit:reload":
			case "/rl":
			case "/bukkit:rl":
			case "/defaultgamemode":
			case "/minecraft:defaultgamemode":
				event.setCancelled(true);
				send.accept("no");
				break;
			case "/cadmin":
			case "/lwc:cadmin":
				event.setCancelled(true);
				redirect.accept("/lwc admin " + argsString);
				break;
		}
	}

}
