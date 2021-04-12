package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

public class VeteranCommand extends CustomCommand {

	public VeteranCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void veteran() {
		line(5);
		send("&3The " + Rank.VETERAN.withColor() + " &3rank is given to &eex-staff &3members to show our appreciation for their help making Bear Nation what it is today.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
