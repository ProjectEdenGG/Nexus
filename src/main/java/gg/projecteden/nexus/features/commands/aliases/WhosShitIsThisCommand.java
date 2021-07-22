package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Disabled // Core doesnt log drops :(
public class WhosShitIsThisCommand extends CustomCommand {

	public WhosShitIsThisCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("pr l a:drop r:10 t:10000d");
	}

}
