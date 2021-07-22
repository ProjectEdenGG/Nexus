package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

@Aliases("realname")
@Description("View the real name of a player from their nickname, uuid, or partial name")
public class NameCommand extends CustomCommand {

	public NameCommand(CommandEvent event) {
		super(event);
	}

	@Path("<partial/uuid/nickname>")
	void uuid(Nerd nerd) {
		send(json("&3Real Name: &e" + nerd.getName())
				.hover("&3Shift+Click to insert into your chat")
				.insert(nerd.getName()));
	}

}
