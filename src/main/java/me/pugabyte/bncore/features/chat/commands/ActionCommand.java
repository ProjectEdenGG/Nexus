package me.pugabyte.bncore.features.chat.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases({"me", "eme", "descibe", "edescribe", "eaction"})
public class ActionCommand extends CustomCommand {

	public ActionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send("&cTemporarily disabled");
	}

}
