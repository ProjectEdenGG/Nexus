package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;

public class OperatorCommand extends CustomCommand {

	public OperatorCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void operator() {
		line(5);
		send("&3&oOperators &3are the second level of staff. Along with all the duties of a mod, they participate in " +
				"&eevent planning &3and take on &especial tasks &3to help improve Bear Nation.");
		line();
		send("&3[+] &eSenior Staff rank");
		send("&3[+] &eHow to achieve&3: &3Promoted from &b&oModerator &3by Senior Staff");
		json("&3[+] &eClick here &3for a list of operators||cmd:/operator list");
		line();
		RanksCommand.ranksReturn(player());
	}

	@Path("list")
	void list() {
		line();
		send("&3All current &3&oOperators &3and the date they were promoted:");
		Rank.OPERATOR.getNerds().forEach(nerd -> {
			send(Rank.OPERATOR.getFormat() + nerd.getName() + " &7-&e " + nerd.getPromotionDate().format(RanksCommand.formatter));
		});
		line();
		RanksCommand.ranksReturn(player());
	}
}
