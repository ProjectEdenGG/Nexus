package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class EconomyCommand extends CustomCommand {

	public EconomyCommand(CommandEvent event) {
		super(event);
	}

	@Path("(sell|selltootherplayers)")
	void sell() {
		line(3);
		send("&3There are a few ways you can trade with other players:");
		send(json2("&3[+] &eTrade signs").url("https://wiki.bnn.gg/wiki/Main_Page#Trade_Sign_Shops").hover("&3Click to open the wiki section on Trade Signs."));
		send(json2("&3[+] &eUse the trading GUI").url("https://wiki.bnn.gg/wiki/Economy#Trading_via_GUI").hover("&3Click to open the wiki section on the trading GUI."));
		send("&3[+] &eSimply ask in chat!");
		line();
		send(json2("&3 « &eClick here to return to the economy menu.").command("/economy"));
	}

	@Path("(cmds|commands)")
	void commands() {
		line(3);
		send("&eEconomy Related Commands");
		send(json2("&3[+] &c/pay <player> <amount>").hover("&3Give someone some money. \nEx: &c/pay notch 666").suggest("/pay "));
		send(json2("&3[+] &c/bal [player]").hover("&3View your balance.\n&3Add a player name to view another player's balance.").suggest("/bal "));
		send(json2("&3[+] &c/baltop [#]").hover("&3View the richest people on the server").suggest("/baltop"));
		send(json2("&3[+] &c/market").hover("&3Visit the market").suggest("/market"));
		line();
		send(json2("&3 « &eClick here to return to the economy menu.").command("/economy"));
	}

	@Path
	void help() {
		line(3);
		send("&3Each player starts out with &e$500&3.");
		send("&3There are multiple ways to make money, such as:");
		line();
		send(json2("&3[+] &eSelling items at the &c/market").suggest("/market"));
		send(json2("&3[+] &eSelling items to other players").command("/economy sell").hover("&3Click for a few tips on how to sell to other players"));
		send(json2("&3[+] &eKilling mobs").url("https://wiki.bnn.gg/wiki/Main_Page#Mobs").hover("&3Click to open the wiki section on mobs."));
		send("&3[+] &eWorking for other players");
		send(json2("&3[+] &eVoting and getting &2&lTop Voter").command("/vote"));
		send(json2("&3[+] &eWinning Events").hover("&3Make sure to check Discord's &e#announcements &3channel and the home page for upcoming events!"));
		line();
		send(json2("&3[+] &eEconomy related commands").command("/economy cmds"));
	}

}
