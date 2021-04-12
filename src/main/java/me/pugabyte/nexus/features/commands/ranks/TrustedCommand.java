package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

public class TrustedCommand extends CustomCommand {

	public TrustedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void trusted() {
		line(5);
		send("&3The " + Rank.TRUSTED.getColor() + "Trusted &3rank is awarded to those who are &eactive &3in the community, are &erespectful &3and &ehelpful &3to others, and follow the rules of the server.");
		line();
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.MEMBER.withColor() + " &3by Staff");
		line();
		RanksCommand.ranksReturn(player());
	}
}
