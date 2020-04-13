package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.Rank;

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

		runCommandAsConsole("lp user " + nerd.getName() + " parent set " + previous.name());
		send(PREFIX + "Demoted " + nerd.getName() + " to " + previous.withColor());
	}

}
