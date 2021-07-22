package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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
