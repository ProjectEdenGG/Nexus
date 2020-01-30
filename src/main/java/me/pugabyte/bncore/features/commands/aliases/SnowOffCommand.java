package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("rainoff")
public class SnowOffCommand extends CustomCommand {

	public SnowOffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		if (player().hasPermission("essentials.pweather")) runCommand("pweather clear");
	}

}
