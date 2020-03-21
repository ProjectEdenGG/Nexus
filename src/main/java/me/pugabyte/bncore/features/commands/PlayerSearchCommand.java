package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerSearchCommand extends CustomCommand {
	NerdService service = new NerdService();

	public PlayerSearchCommand(CommandEvent event) {
		super(event);
	}

	@Path("<name> [amount]")
	void search(String search, @Arg("25") int limit) {
		if (search.length() < 3)
			error("Please be more specific!");

		List<Nerd> nerds = service.find(search).stream().limit(limit).collect(Collectors.toList());
		if (nerds.size() == 0)
			error("No matches found for &e" + search);

		send("&3Matches for '&e" + search + "&3' (&e" + nerds.size() + "&3):");
		for (Nerd nerd : nerds)
			send(json("&e" + nerd.getName()).insert(nerd.getName()));
		send("&3Shift+Click on a name to insert it into your chat");
	}

}
