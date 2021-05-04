package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class IPCommand extends CustomCommand {

	public IPCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void ip() {
		send(json("&eprojecteden.gg").hover("Click to copy").copy("projecteden.gg"));
	}
}
