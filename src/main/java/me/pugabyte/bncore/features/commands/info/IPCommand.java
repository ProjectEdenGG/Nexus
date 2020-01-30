package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class IPCommand extends CustomCommand {

	public IPCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void ip() {
		send("&ebnn.gg");
	}
}
