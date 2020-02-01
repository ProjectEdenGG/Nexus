package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("serverinfo")
public class HelpCommand extends CustomCommand {

	public HelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("&eHello there, and welcome to the server, &b" + player().getName() + "&e.");
		send("&eGot a question? &3Just ask! or &e&lclick below &3for the fastest, most in-depth answers:");
		line();
		send(json2("&3[+] &eFAQ").command("/faq"));
		send(json2("&3[+] &eRules").command("/rules"));
		send(json2("&3[+] &eRanks").command("/ranks"));
		send(json2("&3[+] &eDiscord").command("/discord"));
		send(json2("&3[+] &eWiki").url("https://wiki.bnn.gg/").hover("&eThis will open our wiki in your browser."));
		send(json2("&3[+] &eCommands").url("https://wiki.bnn.gg/wiki/Commands").hover("&eThis will open a page from our wiki in your browser."));
		send(json2("&3[+] &eProtection").command("/protection"));
		send(json2("&3[+] &eHome related commands").command("/homehelp"));
		send(json2("&3[+] &eEconomy").command("/economy"));
		send(json2("&3[+] &eVote").command("/vote"));
		send(json2("&3[+] &eStaff help commands").command("/staffhelpcommands"));
		line();
		send(json2("&3If you have any questions, please ask. Enjoy the server!"));

	}

}
