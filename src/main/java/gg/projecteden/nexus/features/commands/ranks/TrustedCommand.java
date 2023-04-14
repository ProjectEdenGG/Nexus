package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class TrustedCommand extends CustomCommand {

	public TrustedCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Trusted rank")
	public void help() {
		line(5);
		send("&3The " + Rank.TRUSTED.getChatColor() + "Trusted &3rank is awarded to those who are &eactive &3in the community, are &erespectful &3and &ehelpful &3to others, and follow the rules of the server.");
		line();
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.MEMBER.getColoredName() + " &3by Staff");
		line();
		RanksCommand.ranksReturn(player());
	}
}
