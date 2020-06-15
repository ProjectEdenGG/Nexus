package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

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
		send(json("&3[+] &eWiki").url("https://wiki.bnn.gg/"));
		runCommandAsConsole("curiosity cookies " + player().getName());
	}

}
