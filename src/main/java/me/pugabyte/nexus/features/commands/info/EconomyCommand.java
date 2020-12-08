package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class EconomyCommand extends CustomCommand {

	public EconomyCommand(CommandEvent event) {
		super(event);
	}

	@Path("selling")
	void sell() {
		line(3);
		send("&3There are a few ways you can trade with other players:");
		send(json("&3[+] &eShops").url("https://wiki.bnn.gg/wiki/Shops").hover("&3Click to open the wiki section on Shops."));
		send("&3[+] &eSimply ask in chat!");
		line();
		send(json("&3 « &eClick here to return to the economy menu.").command("/economy"));
	}

	@Path("commands")
	void commands() {
		line(3);
		send("&eEconomy Related Commands");
		send(json("&3[+] &c/pay <player> <amount>").hover("&3Give someone some money. \nEx: &c/pay notch 666").suggest("/pay "));
		send(json("&3[+] &c/bal [player]").hover("&3View your balance.\n&3Add a player name to view another player's balance.").suggest("/bal "));
		send(json("&3[+] &c/baltop [#]").hover("&3View the richest people on the server").suggest("/baltop"));
		send(json("&3[+] &c/market").hover("&3Visit the market").suggest("/market"));
		line();
		send(json("&3 « &eClick here to return to the economy menu.").command("/economy"));
	}

	@Path
	@Override
	public void help() {
		line(3);
		send("&3Each player starts out with &e$500&3.");
		send("&3There are multiple ways to make money, such as:");
		line();
		send(json("&3[+] &eSelling items at the &c/market").suggest("/market"));
		send(json("&3[+] &eSelling items at the &c/market &3in the &eresource world").hover("&3Non auto-farmable resources sell for more in this world").suggest("/warp resource"));
		send(json("&3[+] &eSelling items to other players").command("/economy selling").hover("&3Click for a few tips on how to sell to other players"));
		send(json("&3[+] &eKilling mobs").url("https://wiki.bnn.gg/wiki/Main_Page#Mobs").hover("&3Click to open the wiki section on mobs."));
		send("&3[+] &eWorking for other players");
		send(json("&3[+] &eVoting and getting &2&lTop Voter").command("/vote"));
		send(json("&3[+] &eWinning Events").hover("&3Make sure to check Discord's &e#announcements &3channel and the home page for upcoming events!"));
		line();
		send(json("&3[+] &eEconomy related commands").command("/economy commands"));
	}

}
