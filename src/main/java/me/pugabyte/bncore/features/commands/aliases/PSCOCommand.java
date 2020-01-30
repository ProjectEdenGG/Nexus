package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class PSCOCommand extends CustomCommand {

	public PSCOCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		runCommand("ps setowner " + player.getName());
	}
}
