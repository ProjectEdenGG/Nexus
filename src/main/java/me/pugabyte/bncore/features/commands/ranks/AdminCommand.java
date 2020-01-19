package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;

public class AdminCommand extends CustomCommand {

	public AdminCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void admin() {
		line(5);
		send("&9&oAdministrator &3is the highest possible rank to achieve on the server. They are in charge of the &eentire &3server and staff, " +
				"and making sure everything is running as it should.");
		line();
		send("&3[+] &eSenior Staff rank");
		send("&3[+] &eHow to achieve&3: &3Promoted from &3&oOperator &3by existing Admins");
		json("&3[+] &eClick here &3for a list of admins||cmd:/admin list");
		line();
		RanksCommand.ranksReturn(player());
	}

	@Path("list")
	void list() {
		line();
		send("&3All current &9&oAdmins &3and the date they were promoted:");
		Rank.ADMIN.getNerds().forEach(nerd -> {
			send(Rank.ADMIN.getFormat() + nerd.getName() + " &7-&e " + nerd.getPromotionDate().format(RanksCommand.formatter));
		});
		line();
		RanksCommand.ranksReturn(player());
	}
}
