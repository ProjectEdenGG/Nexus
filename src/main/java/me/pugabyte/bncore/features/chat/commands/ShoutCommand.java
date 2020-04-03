package me.pugabyte.bncore.features.chat.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;

public class ShoutCommand extends CustomCommand {
	private Chatter chatter;

	public ShoutCommand(CommandEvent event) {
		super(event);
		chatter = new ChatService().get(player());
	}

	@Path
	void run() {
		runCommand("ch qm g " + argsString());
//		chatter.say(ChatManager.getMainChannel(), argsString());
	}
}
