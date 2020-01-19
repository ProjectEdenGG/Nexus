package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class OwnerCommand extends CustomCommand {

	public OwnerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void owner() {
		line(5);
		send("&4&oPugabyte&3, &efounder &3of the server. Along with the &9&oAdmin &3duties, he does most of the &eplugin &3management, " +
				"&edevelops &3new aspects of the server, and uses donations to buy cool new things for the server.");
		line();
		send("&3[+] &eSenior Staff rank");
		line();
		RanksCommand.ranksReturn(player());
	}
}
