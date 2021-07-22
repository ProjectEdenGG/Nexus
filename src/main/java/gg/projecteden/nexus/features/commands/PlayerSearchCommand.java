package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;

import java.util.List;
import java.util.stream.Collectors;

@Description("Search for a player with a partial name")
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
