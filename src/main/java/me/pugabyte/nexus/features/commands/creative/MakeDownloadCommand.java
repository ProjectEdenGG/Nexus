package me.pugabyte.nexus.features.commands.creative;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class MakeDownloadCommand extends CustomCommand {

	public MakeDownloadCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		runCommandAsOp(player, "plot download");
	}
}
