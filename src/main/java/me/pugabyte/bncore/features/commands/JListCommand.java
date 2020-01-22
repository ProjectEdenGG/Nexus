package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

//@Aliases({"ls", "who", "online", "players", "eonline", "elist", "ewho"})
public class JListCommand extends CustomCommand {

	public JListCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {

	}
}
