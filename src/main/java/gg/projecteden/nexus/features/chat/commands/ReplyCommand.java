package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NonNull;

@Aliases("r")
public class ReplyCommand extends CustomCommand {
	private final Chatter chatter;

	public ReplyCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatterService().get(player());
	}

	@Path("[message...]")
	@Description("Reply to your last private message")
	void reply(String message) {
		if (chatter.getLastPrivateMessage() == null)
			error("No one has messaged you");

		if (Nullables.isNullOrEmpty(message))
			chatter.setActiveChannel(chatter.getLastPrivateMessage());
		else
			chatter.say(chatter.getLastPrivateMessage(), message);
	}

	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	@Override
	@Path("help")
	public void help() {
		reply(arg(1));
	}

	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	@Path("help [message...]")
	public void help(String message) {
		reply(arg(1) + " " + arg(2));
	}
}
