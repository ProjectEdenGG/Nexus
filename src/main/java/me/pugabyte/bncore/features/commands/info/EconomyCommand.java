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
		line();
		send("&3There are a few ways you can trade with other players:");
		json("&3[+] &eTrade signs||url:https://wiki.bnn.gg/wiki/Main_Page##Trade_Sign_Shops||ttp:&3Click to open the wiki " +
				"\n&3section on Trade Signs.");
		json("&3[+] &eUse the trading GUI||url:https://wiki.bnn.gg/wiki/Economy##Trading_via_GUI||ttp:&3Click to open the wiki " +
				"\n&3section on the trading GUI.");
		send("&3[+] &eSimply ask in chat!");
		line();
		json("&3 « &eClick here to return to the economy menu.||cmd:/economy");
	}

	@Path("(cmds|commands)")
	void commands() {
		line();
		send("&eEconomy Related Commands");
		json("&3[+] &c/pay <player> <amount>||ttp:&3Give someone some money. " +
				"\n&3Ex: &c/pay notch 666||sgt:/pay ");
		json("&3[+] &c/bal [player]||ttp:&3View your balance. " +
				"\n&3Add a player name to view " +
				"\n&3another player's balance.||sgt:/bal ");
		json("&3[+] &c/baltop [##]||ttp:&3View the richest people on the server||sgt:/baltop");
		json("&3[+] &c/market||ttp:&3Visit the market||sgt:/market");
		line();
		json("&3 « &eClick here to return to the economy menu.||cmd:/economy");
	}

	@Path
	void help() {
		line();
		send("&3Each player starts out with &e$500&3.");
		send("&3There are multiple ways to make money, such as:");
		line();
		json("&3[+] &eSelling items at the &c/market||sgt:/market");
		json("&3[+] &eSelling items to other players||cmd:/economy sell||ttp:&3Click for a few tips on how " +
				"\n&3to sell to other players");
		json("&3[+] &eKilling mobs||url:https://wiki.bnn.gg/wiki/Main_Page##Mobs||ttp:&3Click to open the wiki " +
				"\n&3section on mobs.");
		send("&3[+] &eWorking for other players");
		json("&3[+] &eVoting and getting &2&lTop Voter||cmd:/vote");
		json("&3[+] &eWinning Events||ttp:&3Make sure to check Discord's " +
				"\n&e##announcements &3channel and the " +
				"\n&3home page for upcoming events!");
		line();
		json("&3[+] &eEconomy related commands||cmd:/economy cmds");
	}

}
