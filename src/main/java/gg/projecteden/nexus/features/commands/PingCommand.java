package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class PingCommand extends CustomCommand {

	public PingCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("Check your connection speed to the server")
	void run(@Arg("self") Player player) {
		send(PREFIX + (isSelf(player) ? "Your" : player.getName() + "'s") + " ping is &e" + player.getPing() + "ms");
	}

}
