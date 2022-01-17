package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

public class UUIDCommand extends CustomCommand {

	public UUIDCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void uuid(@Arg("self") Nerd nerd) {
		send(json("&e" + nerd.getUuid())
				.hover("&3Shift+Click to insert into your chat")
				.insert(nerd.getUuid().toString()));
	}

}
