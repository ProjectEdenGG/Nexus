package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.STAFF)
public class WhosShitIsThisCommand extends CustomCommand {

	public WhosShitIsThisCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("co l a:-item r:10 t:30y");
	}

}
