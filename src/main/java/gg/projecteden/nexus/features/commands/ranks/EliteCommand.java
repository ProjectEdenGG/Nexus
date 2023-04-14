package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class EliteCommand extends CustomCommand {

	public EliteCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Elite rank")
	public void help() {
		line(5);
		send("&3The " + Rank.ELITE.getColoredName() + " &3rank is given to " + Rank.TRUSTED.getColoredName() + " &3players who go &eabove and beyond &3in trying to help other people, and &eimpact the server &3in a &epositive &3way.");
		line();
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.TRUSTED.getColoredName() + " &3by Staff");
		line();
		RanksCommand.ranksReturn(player());
	}
}
