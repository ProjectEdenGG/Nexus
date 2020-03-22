package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.StringUtils;

@Aliases("lastquit")
public class LastLogoutCommand extends CustomCommand {

	public LastLogoutCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void lastLogout(@Arg("self") Nerd nerd) {
		send("&e&l" + nerd.getName() + " &3last logged out &e" + StringUtils.timespanDiff(nerd.getLastQuit()) + " &3ago");
	}
}
