package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

@HideFromWiki
public class GriffinWelcCommand extends CustomCommand {

	public GriffinWelcCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View resources to help you get started")
	void run() {
		line(2);
		send("&eHello there, and welcome to the server!");
		send("&3Here's a few resources you may find helpful as you explore our server. Just &e&lclick &3to open them");
		send(json("&3[+] &eFAQ").command("/faq"));
		send(json("&3[+] &eRules").command("/rules"));
		send(json("&3[+] &eWiki").url(WikiType.SERVER.getUrl()));
	}

}
