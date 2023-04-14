package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class GuestCommand extends CustomCommand {

	public GuestCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Guest rank")
	public void help() {
		line(5);
		send(Rank.GUEST.getColoredName() + " &3is the rank &eeveryone &3starts out as. Guests have all &ebasic &3permissions, nothing special.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
