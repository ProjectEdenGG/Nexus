package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;

public class PlayerSearchCommand extends CustomCommand {

	public PlayerSearchCommand(CommandEvent event) {
		super(event);
	}

	@Path("<name> [amount]")
	void search(String string, @Arg("25") int limit) {
		NerdService service = new NerdService();
		send("&3Matches for '&e" + arg(1) + "&3':");
		for (Nerd nerd : service.search("name", arg(1), limit)) {
			send(json("&e" + nerd.getName()).suggest(nerd.getName()));
		}
		send("&3Click on a name to insert it into your chat");
	}

	@Path
	void usage() {
		error("Usage: /playersearch <partial name>");
	}

}
