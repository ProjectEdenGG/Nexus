package me.pugabyte.nexus.features.radar;

import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Fallback;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;

@Fallback("aac")
@Permission("group.admin")
public class AACCommand extends CustomCommand {

	public AACCommand(CommandEvent event) {
		super(event);
	}

	@Path("notify <player> <message...>")
	void notify(Player player, String reason) {
		String name = player.getName();
		WorldGroup worldGroup = WorldGroup.get(player);

		if (worldGroup == WorldGroup.SURVIVAL)
			if (reason.contains("interacting suspiciously") || reason.contains("attacking out of their line of sight"))
				return;

		String[] numbers = reason.substring(reason.indexOf("{worldgroup}")).replaceAll("[()VL:msTPS]", "").split(",");
		double ping = Double.parseDouble(numbers[1].trim());
		double tps = Double.parseDouble(numbers[2].trim());
		if (ping < 200 && tps >= 18) {
			String message = "&a" + name + " &f" + reason.replace("{worldgroup}", camelCase(worldGroup));
			Chat.broadcastIngame("&7&l[&cRadar&7&l] " + message, StaticChannel.STAFF);
			Chat.broadcastDiscord("**[Radar]** " + message, StaticChannel.STAFF);
		}
	}
}
