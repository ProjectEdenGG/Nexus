package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.nerd.Nerd;

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

		runCommandAsConsole("pex user " + nerd.getName() + " group set " + previous);
		send(PREFIX + "Demoted " + nerd.getName() + " to " + previous.withColor());
	}

}
