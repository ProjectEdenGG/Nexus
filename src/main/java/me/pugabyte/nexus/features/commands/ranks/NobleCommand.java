package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

public class NobleCommand extends CustomCommand {

	public NobleCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void noble() {
		line(5);
		send("&3The " + Rank.NOBLE.withColor() + " &3rank is given to long-time " + Rank.ELITE.withColor() + "&3 and " + Rank.VETERAN.withColor() + "&3 players who have&e excelled&3 at helping out the server through&e community participation&3,&e projects&3, and other means. " +
				"Noble users have access to several staff channels where they can chime in&e ideas for projects&3 and help give input on&e new features and events&3 we're working on before they're released.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
