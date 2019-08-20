package me.pugabyte.bncore.features.wiki;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("wiki")
@NoArgsConstructor
public class WikiCommand extends CustomCommand {

	WikiCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		reply("&eVisit our wiki at &3https://wiki.bnn.gg");
		reply("&eOr use &c/wiki search <query> &eto search the wiki from ingame.");
	}

	@Path("search {string...}")
	void search(@Arg String search) {
		Wiki.search(sender(), search.split(" "), "Wiki");
	}
}
