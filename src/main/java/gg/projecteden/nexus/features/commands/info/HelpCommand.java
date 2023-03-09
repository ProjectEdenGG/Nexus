package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Aliases("serverinfo")
public class HelpCommand extends CustomCommand {

	public HelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	@Description("View some basic commands of the server")
	public void help() {
		send("&eHello there, and welcome to the server, &b" + name() + "&e.");
		send("&eGot a question? &3Just ask! or &e&lclick below &3for the fastest, most in-depth answers:");
		line();
		send(json("&3[+] &eFAQ").command("/faq"));
		send(json("&3[+] &eRules").command("/rules"));
		send(json("&3[+] &eRanks").command("/ranks"));
		send(json("&3[+] &eDiscord").command("/discord"));
		send(json("&3[+] &eWiki").url(WikiType.SERVER.getUrl()).hover("&eThis will open our wiki in your browser."));
		send(json("&3[+] &eCommands").url(WikiType.SERVER.getBasePath() + "Commands").hover("&eThis will open a page from our wiki in your browser."));
		send(json("&3[+] &eProtection").command("/protection"));
		send(json("&3[+] &eHome related commands").command("/homes help"));
		send(json("&3[+] &eEconomy").command("/economy"));
		send(json("&3[+] &eVote").command("/vote"));
		send(json("&3[+] &eStaff help commands").command("/staffhelp"));
		line();
		send(json("&3If you have any questions, please ask. Enjoy the server!"));
	}

}
