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
		send("&3Most likely you are asking yourself the question '&eWhy should I choose this server?&3'");
		send("&3I can promise you that if you &estick around &3long enough and &egive us a chance&3, you won't be disappointed");
		send("&3Here's a few resources you may find helpful as you explore our server. Just &e&lclick &3to open them");
		send(json2("&3[+] &eFAQ").command("/faq"));
		send(json2("&3[+] &eRules").command("/rules"));
		send(json2("&3[+] &eWiki").url("https://wiki.bnn.gg/"));
		runCommand("curiositycookies");
	}

}
