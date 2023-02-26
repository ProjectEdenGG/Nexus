package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Description("Creates a ticket and requests a download of your creative plot for you.")
public class DLRequestCommand extends CustomCommand {

	public DLRequestCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!world().getName().equalsIgnoreCase("creative"))
			error("You must be in the creative world to run this command.");

		runCommand("ticket Plot download request");
	}
}
