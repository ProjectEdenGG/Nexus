package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.Rank;

@Permission("group.seniorstaff")
public class PromoteCommand extends CustomCommand {

	public PromoteCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void promote(Nerd nerd) {
		Rank rank = nerd.getRank();
		Rank next = rank.next();
		if (rank == next)
			error("User is already max rank");

		runCommandAsConsole("lp user " + nerd.getName() + " parent set " + next.name());
		send(PREFIX + "Promoted " + nerd.getName() + " to " + next.withColor());
	}

}
