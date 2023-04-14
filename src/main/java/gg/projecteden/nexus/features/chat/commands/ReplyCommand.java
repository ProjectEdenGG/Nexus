package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.HideFromHelp;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import lombok.NonNull;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Aliases("r")
public class ReplyCommand extends CustomCommand {
	private final Chatter chatter;

	public ReplyCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatterService().get(player());
	}

	@NoLiterals
	@Description("Reply to your last private message")
	void reply(@Optional @Vararg String message) {
		if (chatter.getLastPrivateMessage() == null)
			error("No one has messaged you");

		if (isNullOrEmpty(message))
			chatter.setActiveChannel(chatter.getLastPrivateMessage());
		else
			chatter.say(chatter.getLastPrivateMessage(), message);
	}

	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	@Override
	@Description("Override help")
	public void help() {
		reply(arg(1));
	}

	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	@Description("Override help")
	public void help(@Optional @Vararg String message) {
		reply(arg(1) + " " + (message == null ? "" : message));
	}
}
