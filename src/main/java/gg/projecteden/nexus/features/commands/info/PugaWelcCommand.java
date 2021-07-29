package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class PugaWelcCommand extends CustomCommand {

	public PugaWelcCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line(2);
		send("&eHello there, and welcome to the server!");
		send("&3Here's a few resources you may find helpful as you explore our server. Just &e&lclick &3to open them");
		send(json("&3[+] &eFAQ").command("/faq"));
		send(json("&3[+] &eRules").command("/rules"));
		send(json("&3[+] &eWiki").url(WikiType.SERVER.getUrl()));
		runCommandAsConsole("curiosity cookies " + name());
	}

}
