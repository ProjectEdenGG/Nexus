package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.StringUtils;

public class ArchitectCommand extends CustomCommand {

	public ArchitectCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void architect() {
		line(5);
		send(Rank.ARCHITECT.getColor() + "Architects &3oversee the building tasks for the server and recruit players to help with projects.");
		line();
		send("&3[+] &eHow to achieve&3: Promoted from " + Rank.BUILDER.getColor() + "Builder &3by Senior Staff.");
		send(json("&3[+] &eClick here &3for a list of architects").command("/architect list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		line();
		send("&3All current " + Rank.ARCHITECT.getColor() + "Architects &3and the date they were promoted:");
		Rank.ARCHITECT.getNerds().forEach(nerd -> {
			send(Rank.ARCHITECT.getColor() + nerd.getName() + " &7-&e " + StringUtils.shortDateFormat(nerd.getPromotionDate()));
		});
		line();
		RanksCommand.ranksReturn(player());
	}
}
