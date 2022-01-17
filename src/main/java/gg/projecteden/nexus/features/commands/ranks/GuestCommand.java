package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

public class GuestCommand extends CustomCommand {

	public GuestCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void guest() {
		line(5);
		send(Rank.GUEST.getColoredName() + " &3is the rank &eeveryone &3starts out as. Guests have all &ebasic &3permissions, nothing special.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
