package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;

public class UUIDCommand extends CustomCommand {

	public UUIDCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void uuid(@Arg("self") Nerd nerd) {
		send(json("&e" + nerd.getUuid())
				.hover("&3Shift+Click to insert into your chat")
				.insert(nerd.getUuid()));
	}

}
