package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.pugabyte.bncore.utils.StringUtils.plural;
import static me.pugabyte.bncore.utils.StringUtils.trimFirst;

public class CommandOverrideListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		List<String> args = Arrays.asList(event.getMessage().split(" "));
		if (args.size() == 0) return;
		String argsString = event.getMessage().replace(args.get(0) + " ", "");

		Consumer<String> redirect = command -> Utils.runCommand(player, trimFirst(command));
		Consumer<String> send = message -> Utils.send(player, message);

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
			case "/minecraft:op":
				event.setCancelled(true);
				send.accept("no");
				break;
			case "/cadmin":
			case "/lwc:cadmin":
				event.setCancelled(true);
				redirect.accept("/lwc admin " + argsString);
				break;
			case "/p2":
			case "/plot":
			case "/plots":
			case "/plotme":
			case "/plotsquared":
				if (args.size() >= 2 && args.get(1).equalsIgnoreCase("limit")) {
					event.setCancelled(true);

					if (WorldGroup.get(player) != WorldGroup.CREATIVE) {
						send.accept("&cYou must be in the /creative world to use this command!");
						break;
					}

					int limit = 0;
					for (int i = 1; i <= 6; i++)
						if (player.hasPermission("plots.plot." + i))
							limit = i;

					if (limit == 0) {
						send.accept("&cYou cannot claim any plots");
						break;
					}

					send.accept("&3You can claim &e" + limit + plural(" &3plot", limit));
				}
				break;
		}
	}

}
