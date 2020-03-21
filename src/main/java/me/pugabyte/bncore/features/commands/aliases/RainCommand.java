package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("snow")
@Fallback("essentials")
@Redirect(from = {"/rainoff", "/snowoff"}, to = "/rain off")
public class RainCommand extends CustomCommand {

	public RainCommand(CommandEvent event) {
		super(event);
	}

	@Path("off")
	void run() {
		runCommand("pweather clear");
	}

}
