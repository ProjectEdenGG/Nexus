package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;

@Aliases("tm")
public class TrademarkCommand extends CustomCommand {
	private final Chatter chatter;

	public TrademarkCommand(CommandEvent event) {
		super(event);
		chatter = new ChatterService().get(player());
	}

	@Path
	@Description("Insert a trademark symbol at the end of your message")
	void run() {
		chatter.say(argsString() + "™");
	}

}
