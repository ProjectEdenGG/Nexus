package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.trimFirst;

public class CommandOverrideListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		List<String> args = Arrays.asList(event.getMessage().split(" "));
		if (args.size() == 0) return;
		String argsString = event.getMessage().replace(args.get(0) + " ", "");

		Consumer<String> redirect = command -> Utils.runCommand(player, trimFirst(command));
		Consumer<String> send = message -> player.sendMessage(colorize(message));

		switch (args.get(0)) {
			case "/pex":
				if (!player.hasPermission("group.seniorstaff")) {
					event.setCancelled(true);
					try {
						new CooldownService().check(player, "pex-lag", Time.MINUTE);
						Chat.broadcast(player.getName() + " may be trying to lag the server", "Staff");
					} catch (CooldownException ignore) {}
				}
				break;
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
