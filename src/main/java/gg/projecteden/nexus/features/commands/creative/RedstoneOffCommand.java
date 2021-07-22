package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class RedstoneOffCommand extends CustomCommand {

	public RedstoneOffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("plots flag set redstone false");
	}

}
