package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.MODERATOR)
public class WhoseShitIsThisCommand extends CustomCommand {

	public WhoseShitIsThisCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Look up who's items are on the ground near you")
	void run() {
		runCommand("co l a:-item r:10 t:30y");
	}

}
