package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;

@Aliases("tm")
public class TrademarkCommand extends CustomCommand {
	private final Chatter chatter;

	public TrademarkCommand(CommandEvent event) {
		super(event);
		chatter = new ChatterService().get(player());
	}

	@NoLiterals
	@Description("Insert a trademark symbol at the end of your message")
	void run(@Optional @Vararg String message) {
		if (message == null)
			message = "";

		chatter.say(message + "™");
	}

}
