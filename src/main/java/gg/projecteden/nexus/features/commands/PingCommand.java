package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class PingCommand extends CustomCommand {

	public PingCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("Check your connection speed to the server")
	void run(@Optional("self") Player player) {
		send(PREFIX + (isSelf(player) ? "Your" : player.getName() + "'s") + " ping is &e" + player.getPing() + "ms");
	}

}
