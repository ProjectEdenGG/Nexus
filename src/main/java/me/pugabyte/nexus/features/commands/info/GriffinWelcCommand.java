package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class GriffinWelcCommand extends CustomCommand {

	public GriffinWelcCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line(2);
		send("&eHello there, and welcome to the server!");
		send("&3Here's a few resources you may find helpful as you explore our server. Just &e&lclick &3to open them");
		send(json("&3[+] &eFAQ").command("/faq"));
		send(json("&3[+] &eRules").command("/rules"));
		send(json("&3[+] &eWiki").url("https://wiki.projecteden.gg/"));
		runCommandAsConsole("curiosity cookies " + name());
	}

}
