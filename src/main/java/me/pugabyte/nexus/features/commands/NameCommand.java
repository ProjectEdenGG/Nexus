package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;

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
