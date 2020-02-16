package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;

import java.util.Comparator;
import java.util.List;

public class PlayerSearchCommand extends CustomCommand {

	public PlayerSearchCommand(CommandEvent event) {
		super(event);
	}

	@Path("<name>")
	void search(String string) {
		NerdService service = new NerdService();
		send("&3Matches for &e" + arg(1));
		List<Nerd> nerds = service.search(arg(1));
		nerds.sort(Comparator.comparing(Nerd::getName));
		for (Nerd nerd : service.search(arg(1))) {
			send(json("&e" + nerd.getName()).suggest(nerd.getName()));
		}
		send("&3Click on a name to insert it into your chat");
	}

	@Path
	void usage() {
		error("Usage: /playersearch <partial name>");
	}

}
