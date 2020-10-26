package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.StringUtils;

public class OperatorCommand extends CustomCommand {

	public OperatorCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void operator() {
		line(5);
		send(Rank.OPERATOR.getFormat() + "Operators &3are the second level of staff. Along with all the duties of a mod, they participate in " +
				"&eevent planning &3and take on &especial tasks &3to help improve Bear Nation.");
		line();
		send("&3[+] &eSenior Staff rank");
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.MODERATOR.getFormat() + "Moderator &3by Senior Staff");
		send(json("&3[+] &eClick here &3for a list of operators").command("/operator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		line();
		send("&3All current " + Rank.OPERATOR.getFormat() + "Operators &3and the date they were promoted:");
		Rank.OPERATOR.getNerds().forEach(nerd -> {
			send(Rank.OPERATOR.getFormat() + nerd.getName() + " &7-&e " + StringUtils.shortDateFormat(nerd.getPromotionDate()));
		});
		line();
		RanksCommand.ranksReturn(player());
	}
}
