package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

public class MemberCommand extends CustomCommand {

	public MemberCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void member() {
		line(5);
		send(Rank.MEMBER.getColoredName() + " &3rank is achieved by playing for a total of &e24 hours&3. Use &c/hours &3to check your play time.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
