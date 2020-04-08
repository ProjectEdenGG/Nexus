package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Fallback("aac")
@Permission("group.admin")
public class AACCommand extends CustomCommand {

	public AACCommand(CommandEvent event) {
		super(event);
	}

	@Path("notify <message...>")
	void notify(String message) {
		Chat.broadcast(message, "staff");
	}
}
