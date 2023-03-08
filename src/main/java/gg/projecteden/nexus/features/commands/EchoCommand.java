package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class EchoCommand extends CustomCommand {

	public EchoCommand(CommandEvent event) {
		super(event);
	}

	@Path("[string...]")
	@Description("Print your colorized input in chat")
	void echo(@Arg(" ") String string) {
		send(string);
	}

}
