package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

public class EliteCommand extends CustomCommand {

	public EliteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void elite() {
		line(5);
		send("&3The " + Rank.ELITE.getColoredName() + " &3rank is given to " + Rank.TRUSTED.getColoredName() + " &3players who go &eabove and beyond &3in trying to help other people, and &eimpact the server &3in a &epositive &3way.");
		line();
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.TRUSTED.getColoredName() + " &3by Staff");
		line();
		RanksCommand.ranksReturn(player());
	}
}
