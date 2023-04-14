package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

public class EchoCommand extends CustomCommand {

	public EchoCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[string...]")
	@Description("Print your colorized input in chat")
	void echo(@Arg(" ") String string) {
		send(string);
	}

}
