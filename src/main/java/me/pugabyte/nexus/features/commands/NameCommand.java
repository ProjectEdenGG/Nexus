package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;

public class NameCommand extends CustomCommand {

	public NameCommand(CommandEvent event) {
		super(event);
	}

	@Path("<partial/uuid/nickname>")
	void uuid(Nerd nerd) {
		send(json("&e" + nerd.getName())
				.hover("&3Shift+Click to insert into your chat")
				.insert(nerd.getName()));
	}

}
