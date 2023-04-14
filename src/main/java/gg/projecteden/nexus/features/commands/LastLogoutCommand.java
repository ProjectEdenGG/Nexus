package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

@Aliases("lastquit")
public class LastLogoutCommand extends CustomCommand {

	public LastLogoutCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("View the last time a player logged out")
	void lastLogout(@Optional("self") Nerd nerd) {
		send("&e&l" + nerd.getNickname() + " &3last logged out &e" + Timespan.of(nerd.getLastQuit(player())).format() + " &3ago");
	}
}
