package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;

public class ShrugCommand extends CustomCommand {
	private final Chatter chatter;

	public ShrugCommand(CommandEvent event) {
		super(event);
		chatter = new ChatterService().get(player());
	}

	@Path
	void run() {
		chatter.say(argsString() + " ¯\\_(ツ)_/¯");
	}

}
