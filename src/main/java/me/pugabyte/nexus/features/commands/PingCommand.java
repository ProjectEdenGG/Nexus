package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class PingCommand extends CustomCommand {

	public PingCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		send(PREFIX + (isSelf(player) ? "Your" : player.getName() + "'s") + " ping is &e" + player.spigot().getPing() + "ms");
	}

}
