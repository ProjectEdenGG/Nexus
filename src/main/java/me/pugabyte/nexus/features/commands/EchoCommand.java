package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class EchoCommand extends CustomCommand {

	public EchoCommand(CommandEvent event) {
		super(event);
	}

	@Path("[string...]")
	void echo(@Arg(" ") String string) {
		send(string);
	}
}
