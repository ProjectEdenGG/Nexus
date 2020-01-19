package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

import java.text.NumberFormat;

public class UniqueCommand extends CustomCommand {

	public UniqueCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void unique() {
		int players = Bukkit.getServer().getOfflinePlayers().length;
		send(NumberFormat.getIntegerInstance().format(players) + " unique players have joined the server");
	}
}
