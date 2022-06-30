package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

@Aliases("lastquit")
public class LastLogoutCommand extends CustomCommand {

	public LastLogoutCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void lastLogout(@Arg("self") Nerd nerd) {
		send("&e&l" + nerd.getNickname() + " &3last logged out &e" + Timespan.of(nerd.getLastQuit(player())).format() + " &3ago");
	}
}
