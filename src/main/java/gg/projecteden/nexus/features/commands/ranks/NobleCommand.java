package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

public class NobleCommand extends CustomCommand {

	public NobleCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void noble() {
		line(5);
		send("&3The " + Rank.NOBLE.getColoredName() + " &3rank is given to long-time " + Rank.ELITE.getColoredName() + "&3 and " + Rank.VETERAN.getColoredName() + "&3 players who have&e excelled&3 at helping out the server through&e community participation&3,&e projects&3, and other means. " +
				"Noble users have access to several staff channels where they can chime in&e ideas for projects&3 and help give input on&e new features and events&3 we're working on before they're released.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
