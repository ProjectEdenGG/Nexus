package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class IPCommand extends CustomCommand {

	public IPCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Sends the server's IP in chat.")
	void ip() {
		send(json("&e" + Nexus.DOMAIN).hover("Click to copy").copy(Nexus.DOMAIN));
	}
}
