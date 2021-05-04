package me.pugabyte.nexus.features.wiki;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class WikiCommand extends CustomCommand {

	WikiCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		send("&eVisit our wiki at &3https://wiki.projecteden.gg");
		send("&eOr use &c/wiki search <query> &eto search the wiki from in-game.");
	}

	@Path("search <query...>")
	void search(String search) {
		Wiki.search(sender(), search.split(" "), "Wiki");
	}
}
