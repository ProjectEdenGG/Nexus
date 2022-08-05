package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class JobsCommand extends CustomCommand {

	public JobsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void soon() {
		send(PREFIX + "Coming soon!");
	}

}
