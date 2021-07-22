package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;

@Permission("group.seniorstaff")
public class DemoteCommand extends CustomCommand {

	public DemoteCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void promote(Nerd nerd) {
		Rank rank = nerd.getRank();
		Rank previous = rank.previous();
		if (rank == previous)
			error("User is already min rank");

		for (Rank _rank : Rank.values())
			runCommandAsConsole("lp user " + nerd.getName() + " parent remove " + _rank.name());
		runCommandAsConsole("lp user " + nerd.getName() + " parent add " + previous.name());
		send(PREFIX + "Demoted " + nerd.getName() + " to " + previous.getColoredName());
	}

}
