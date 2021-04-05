package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;

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

		for (Rank _rank : Rank.values())
			runCommandAsConsole("lp user " + nerd.getName() + " parent remove " + _rank.name());
		runCommandAsConsole("lp user " + nerd.getName() + " parent add " + next.name());
		send(PREFIX + "Promoted " + nerd.getName() + " to " + next.withColor());

		if (nerd.getOfflinePlayer().isOnline())
			Jingle.RANKUP.play(nerd.getPlayer());
	}

}
