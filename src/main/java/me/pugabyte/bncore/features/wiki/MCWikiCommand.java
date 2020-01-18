package me.pugabyte.bncore.features.wiki;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("minecraftwiki")
public class MCWikiCommand extends CustomCommand {

	MCWikiCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		reply("&eVisit the minecraft wiki at &3https://minecraft.gamepedia.com/");
		reply("&eOr use &c/mcwiki search <query> &eto search the wiki from ingame.");
	}

	@Path("search <query...>")
	void search(@Arg String search) {
		Wiki.search(sender(), search.split(" "), "MCWiki");
	}
}
