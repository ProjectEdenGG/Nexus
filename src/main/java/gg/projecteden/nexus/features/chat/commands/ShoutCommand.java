package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;

@HideFromWiki
public class ShoutCommand extends CustomCommand {
	private final Chatter chatter;

	public ShoutCommand(CommandEvent event) {
		super(event);
		chatter = new ChatterService().get(player());
	}

	@NoLiterals
	@Description("Send a message to Global")
	void run(@Vararg String message) {
		chatter.say(ChatManager.getMainChannel(), message);
	}
}
