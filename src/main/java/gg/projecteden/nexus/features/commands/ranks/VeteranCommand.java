package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class VeteranCommand extends CustomCommand {

	public VeteranCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void veteran() {
		line(5);
		send("&3The " + Rank.VETERAN.getColoredName() + " &3rank is given to &eex-staff &3members to show our appreciation for their help making Project Eden what it is today.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
