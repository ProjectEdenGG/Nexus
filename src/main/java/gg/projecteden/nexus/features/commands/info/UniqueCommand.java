package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import org.bukkit.Bukkit;

import java.text.NumberFormat;

public class UniqueCommand extends CustomCommand {

	public UniqueCommand(CommandEvent event) {
		super(event);
	}

	@Async
	@NoLiterals
	@Description("View how many unique players have joined the server")
	void run() {
		int players = Bukkit.getServer().getOfflinePlayers().length;
		send(NumberFormat.getIntegerInstance().format(players) + " unique players have joined the server");
	}
}
