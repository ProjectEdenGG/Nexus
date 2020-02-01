package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.utils.Utils;

public class ArchitectCommand extends CustomCommand {

	public ArchitectCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void architect() {
		line(5);
		send("&5&lArchitects &3oversee the building tasks for the server and recruit players to help with projects.");
		line();
		send("&3[+] &eHow to achieve&3: Promoted from &5Builder &3by Senior Staff.");
		send(json2("&3[+] &eClick here &3for a list of architects").command("/architect list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Path("list")
	void list() {
		line();
		send("&3All current &5Architects &3and the date they were promoted:");
		Rank.ARCHITECT.getNerds().forEach(nerd -> {
			send(Rank.ARCHITECT.getFormat() + nerd.getName() + " &7-&e " + Utils.shortDateFormat(nerd.getPromotionDate()));
		});
		line();
		RanksCommand.ranksReturn(player());
	}
}
