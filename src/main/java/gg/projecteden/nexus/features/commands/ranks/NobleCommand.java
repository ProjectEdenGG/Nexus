package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class NobleCommand extends CustomCommand {

	public NobleCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Noble rank")
	public void help() {
		line(5);
		send("&3The " + Rank.NOBLE.getColoredName() + " &3rank is given to long-time " + Rank.ELITE.getColoredName() + "&3 and " + Rank.VETERAN.getColoredName() + "&3 players who have&e excelled&3 at helping out the server through&e community participation&3,&e projects&3, and other means. " +
				"Noble users have access to several staff channels where they can chime in&e ideas for projects&3 and help give input on&e new features and events&3 we're working on before they're released.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
