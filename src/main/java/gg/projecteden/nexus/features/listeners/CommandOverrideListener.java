package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CommandOverrideListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		List<String> args = Arrays.asList(event.getMessage().split(" "));
		if (args.size() == 0) return;
		String argsString = event.getMessage().replace(args.get(0) + " ", "");

		Consumer<String> redirect = command -> PlayerUtils.runCommand(player, StringUtils.trimFirst(command));
		Consumer<String> send = message -> PlayerUtils.send(player, message);

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
			case "/sc":
			case "/silent":
			case "/silentchest":
			case "/silentcontainer":
			case "/openinv:sc":
			case "/openinv:silent":
			case "/openinv:silentchest":
			case "/openinv:silentcontainer":
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

					if (WorldGroup.of(player) != WorldGroup.SERVER) {
						if (WorldGroup.of(player) != WorldGroup.CREATIVE) {
							send.accept("&cYou must be in the /creative world to use this command!");
							break;
						}
					}

					int limit = 0;
					for (int i = 1; i <= 99; i++)
						if (player.hasPermission("plots.plot." + i))
							limit = i;

					if (limit == 0) {
						send.accept("&cYou cannot claim any plots");
						break;
					}

					send.accept("&3You can claim &e" + limit + StringUtils.plural(" &3plot", limit));
				}
				break;
		}
	}

}
