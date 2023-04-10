package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;

@HideFromWiki
public class ShoutCommand extends CustomCommand {
	private final Chatter chatter;

	public ShoutCommand(CommandEvent event) {
		super(event);
		chatter = new ChatterService().get(player());
	}

	@Path("<message...>")
	void run(String message) {
		chatter.say(ChatManager.getMainChannel(), message);
	}
}
