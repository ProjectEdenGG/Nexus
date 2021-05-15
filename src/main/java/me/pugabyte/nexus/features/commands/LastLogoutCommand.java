package me.pugabyte.nexus.features.commands;

import eden.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;

@Aliases("lastquit")
public class LastLogoutCommand extends CustomCommand {

	public LastLogoutCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void lastLogout(@Arg("self") Nerd nerd) {
		send("&e&l" + Nickname.of(nerd) + " &3last logged out &e" + Timespan.of(nerd.getLastQuit()).format() + " &3ago");
	}
}
