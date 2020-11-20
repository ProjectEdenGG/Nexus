package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

import java.text.NumberFormat;

public class UniqueCommand extends CustomCommand {

	public UniqueCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		int players = Bukkit.getServer().getOfflinePlayers().length;
		send(NumberFormat.getIntegerInstance().format(players) + " unique players have joined the server");
	}
}
