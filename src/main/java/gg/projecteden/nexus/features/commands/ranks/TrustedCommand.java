package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class TrustedCommand extends CustomCommand {

	public TrustedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void trusted() {
		line(5);
		send("&3The " + Rank.TRUSTED.getChatColor() + "Trusted &3rank is awarded to those who are &eactive &3in the community, are &erespectful &3and &ehelpful &3to others, and follow the rules of the server.");
		line();
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.MEMBER.getColoredName() + " &3by Staff");
		line();
		RanksCommand.ranksReturn(player());
	}
}
