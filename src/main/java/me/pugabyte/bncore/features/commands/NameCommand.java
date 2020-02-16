package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;

import java.util.UUID;

public class NameCommand extends CustomCommand {

	public NameCommand(CommandEvent event) {
		super(event);
	}

	@Path("[uuid]")
	void uuid(String string) {
		UUID uuid = UUID.fromString(string);
		if (Utils.getPlayer(uuid).getName() == null) error("Invalid UUID");
		send(json("&e" + Utils.getPlayer(uuid).getName()).hover("&3Click to copy").suggest(Utils.getPlayer(uuid).getName()));
	}

	@Path()
	void usage() {
		error("Usage: /name <uuid>");
	}

}
