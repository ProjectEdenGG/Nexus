package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class MemberCommand extends CustomCommand {

	public MemberCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Member rank")
	public void help() {
		line(5);
		send(Rank.MEMBER.getColoredName() + " &3rank is achieved by playing for a total of &e24 hours&3. Use &c/hours &3to check your play time.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
