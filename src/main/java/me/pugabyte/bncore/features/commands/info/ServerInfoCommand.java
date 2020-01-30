package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("help")
public class ServerInfoCommand extends CustomCommand {

	public ServerInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("&eHello there, and welcome to the server, &b" + player().getName() + "&e.");
		send("&eGot a question? &3Just ask! or &e&lclick below &3for the fastest, most in-depth answers:");
		line();
		json("&3[+] &eFAQ||cmd:/faq");
		json("&3[+] &eRules||cmd:/rules");
		json("&3[+] &eRanks||cmd:/ranks");
		json("&3[+] &eDiscord||cmd:/discord");
		json("&3[+] &eWiki||url:https://wiki.bnn.gg/||ttp:&eThis will open our wiki in your browser.");
		json("&3[+] &eCommands||url:https://wiki.bnn.gg/wiki/Commands||ttp:&eThis will open a page from our wiki in your browser.");
		json("&3[+] &eProtection||cmd:/protection");
		json("&3[+] &eHome related commands||cmd:/homehelp");
		json("&3[+] &eEconomy||cmd:/economy");
		json("&3[+] &eVote||cmd:/vote");
		json("&3[+] &eStaff help commands||cmd:/staffhelpcommands");
		line();
		json("&3If you have any questions, please ask. Enjoy the server!");

	}

}
