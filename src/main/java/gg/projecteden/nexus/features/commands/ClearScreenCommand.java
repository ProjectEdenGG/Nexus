package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Aliases("cls")
public class ClearScreenCommand extends CustomCommand {

	public ClearScreenCommand(CommandEvent event) {
		super(event);
	}

	@Path("[lines]")
	@Description("Clear your chat")
	void help(@Arg("20") Integer lines) {
		for (int i = 0; i < lines; i++)
			send("");
	}
}
