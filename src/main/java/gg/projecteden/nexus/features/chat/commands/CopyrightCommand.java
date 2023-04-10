package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;

public class CopyrightCommand extends CustomCommand {
	private final Chatter chatter;

	public CopyrightCommand(CommandEvent event) {
		super(event);
		chatter = new ChatterService().get(player());
	}

	@Path("[message...]")
	@Description("Insert a copyright symbol at the end of your message")
	void run(String message) {
		if (message == null)
			message = "";

		chatter.say(message + "©");
	}

}
