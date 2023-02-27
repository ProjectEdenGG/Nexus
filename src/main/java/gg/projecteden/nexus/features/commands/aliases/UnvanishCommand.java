package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.STAFF)
public class UnvanishCommand extends CustomCommand {

	public UnvanishCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Turns vanish off")
	void run() {
		runCommand("vanish off");
	}

	@Path("gameworld")
	@Description("Turns vanish off and puts you in the Minigames channel")
	void gameworld() {
		runCommand("vanish off");
		runCommand("ch join m");
	}

	@Path("creative")
	@Description("Turns vanish off and puts you in the Creative channel")
	void creative() {
		runCommand("vanish off");
		runCommand("ch join c");
	}
}
