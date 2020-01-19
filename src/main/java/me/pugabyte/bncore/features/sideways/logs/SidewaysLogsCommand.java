package me.pugabyte.bncore.features.sideways.logs;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

import static me.pugabyte.bncore.features.sideways.logs.SidewaysLogs.enabledPlayers;

@Aliases("swl")
public class SidewaysLogsCommand extends CustomCommand {

	SidewaysLogsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void toggle() {
		toggle(enabledPlayers.contains(player()));
	}

	@Path("<true|false>")
	void toggle(@Arg boolean normal) {
		if (normal) {
			enabledPlayers.remove(player());
			send(PREFIX + "Now placing logs normally");
		} else {
			enabledPlayers.add(player());
			send(PREFIX + "Now placing logs vertically only");
		}
	}
}
