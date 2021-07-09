package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

import static eden.utils.TimeUtils.shortDateFormat;

public class OperatorCommand extends CustomCommand {

	public OperatorCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void operator() {
		line(5);
		send(Rank.OPERATOR.getChatColor() + "Operators &3are the second level of staff. Along with all the duties of a mod, they participate in " +
				"&eevent planning &3and take on &especial tasks &3to help improve Project Eden.");
		line();
		send("&3[+] &eSenior Staff rank");
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.MODERATOR.getColoredName() + " &3by Senior Staff");
		send(json("&3[+] &eClick here &3for a list of operators").command("/operator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		line();
		send("&3All current " + Rank.OPERATOR.getChatColor() + "Operators &3and the date they were promoted:");
		Rank.OPERATOR.getNerds().forEach(nerd ->
				send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
		line();
		RanksCommand.ranksReturn(player());
	}
}
