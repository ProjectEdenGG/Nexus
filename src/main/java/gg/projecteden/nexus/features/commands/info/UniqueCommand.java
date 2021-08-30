package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

import java.text.NumberFormat;

public class UniqueCommand extends CustomCommand {

	public UniqueCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Async
	void run() {
		int players = Bukkit.getServer().getOfflinePlayers().length;
		send(NumberFormat.getIntegerInstance().format(players) + " unique players have joined the server");
	}
}
