package me.pugabyte.bncore.features.commands.creative;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class MakeDownloadCommand extends CustomCommand {

	public MakeDownloadCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		runCommandAsOp("plot download");
	}
}
