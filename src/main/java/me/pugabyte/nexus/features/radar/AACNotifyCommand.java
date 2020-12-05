package me.pugabyte.nexus.features.radar;

import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

@Permission("group.admin")
public class AACNotifyCommand extends CustomCommand {

	public AACNotifyCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> <message...>")
	void notify(Player player, String reason) {
		String name = player.getName();

		WorldGroup worldGroup = WorldGroup.get(player);
		int ping = player.spigot().getPing();
		double tps = Bukkit.getTPS()[0];

		if (ping < 200 && tps >= 18) {
			String message = "&a" + name + " &f" + reason
					.replace("{worldgroup}", camelCase(worldGroup))
					.replace("{ping}", String.valueOf(ping))
					.replace("{tps}", new DecimalFormat("0.00").format(tps));
			Chat.broadcastIngame("&7&l[&cRadar&7&l] " + message, StaticChannel.STAFF);
			Chat.broadcastDiscord("**[Radar]** " + message, StaticChannel.STAFF);
		}
	}

}
