package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

public class IPCommand extends CustomCommand {

	public IPCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("View the server's IP")
	public void help() {
		send(json("&e" + Nexus.DOMAIN).hover("Click to copy").copy(Nexus.DOMAIN));
	}
}
