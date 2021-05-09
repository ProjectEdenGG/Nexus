package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;

public class OwnerCommand extends CustomCommand {

	public OwnerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void owner() {
		line(5);
		send(Dev.GRIFFIN.getNerd().getColoredName() + "&3, &efounder &3of the server. Along with the " + Rank.ADMIN.getColoredName() + " &3duties, he does most of the &eplugin &3management, " +
				"&edevelops &3new aspects of the server, and uses donations to buy cool new things for the server.");
		line();
		send("&3[+] &eSenior Staff rank");
		line();
		RanksCommand.ranksReturn(player());
	}
}
