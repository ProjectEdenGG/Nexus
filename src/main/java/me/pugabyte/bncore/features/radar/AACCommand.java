package me.pugabyte.bncore.features.radar;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;
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
		String worldGroup = WorldGroup.get(player).name();
		String message = "&a" + name + " &f" + reason.replace("{worldgroup}", worldGroup);
		Chat.broadcast(message, "staff");
	}
}
