package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class UUIDCommand extends CustomCommand {

	public UUIDCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void uuid(@Arg("self") Player player) {
		send(json("&e" + player.getUniqueId()).hover("&3Click to copy").suggest(player.getUniqueId().toString()));
	}

	@Path
	void usage() {
		error("Usage: /uuid <name>");
	}

}
