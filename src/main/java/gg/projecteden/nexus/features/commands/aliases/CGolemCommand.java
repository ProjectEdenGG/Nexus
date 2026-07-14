package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class CGolemCommand extends CustomCommand {

	public CGolemCommand(CommandEvent event) {
		super(event);
	}

	@Path("<state>")
	@Description("Set whether copper golems can access containers")
	void run(boolean state) {
		runCommand("lwc flag golem " + (state ? "on" : "off"));
	}
}
