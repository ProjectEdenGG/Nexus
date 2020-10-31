package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Rank;

public class VeteranCommand extends CustomCommand {

	public VeteranCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void veteran() {
		line(5);
		send("&3The " + Rank.VETERAN.getColor() + "Veteran &3rank is given to &eex-staff &3members, to show our appreciation for their help making Bear Nation what it is today.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
