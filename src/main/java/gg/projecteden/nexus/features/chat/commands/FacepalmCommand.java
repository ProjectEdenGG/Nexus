package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;

public class FacepalmCommand extends CustomCommand {
	private final Chatter chatter;

	public FacepalmCommand(CommandEvent event) {
		super(event);
		chatter = new ChatterService().get(player());
	}

	@Path("[message...]")
	@Description("Insert a facepalm emote at the end of your message")
	void run(String message) {
		chatter.say(message + " (ლ‸－)");
	}

}
