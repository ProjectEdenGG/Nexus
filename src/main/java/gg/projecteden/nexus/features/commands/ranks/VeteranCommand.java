package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class VeteranCommand extends CustomCommand {

	public VeteranCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Veteran rank")
	public void help() {
		line(5);
		send("&3The " + Rank.VETERAN.getColoredName() + " &3rank is given to &eex-staff &3members to show our appreciation for their help making Project Eden what it is today.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
