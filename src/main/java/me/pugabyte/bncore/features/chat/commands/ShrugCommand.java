package me.pugabyte.bncore.features.chat.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;

public class ShrugCommand extends CustomCommand {
	private Chatter chatter;

	public ShrugCommand(CommandEvent event) {
		super(event);
		chatter = new ChatService().get(player());
	}

	@Path
	void run() {
		runCommandAsConsole("essentials:sudo " + player().getName() + " c:" + argsString() + " ¯\\_(ツ)_/¯");
//		chatter.say(argsString() + " ¯\\_(ツ)_/¯");
	}

}
