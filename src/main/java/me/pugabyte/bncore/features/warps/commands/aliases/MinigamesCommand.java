package me.pugabyte.bncore.features.warps.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;

@Aliases({"gamelobby", "gl"})
public class MinigamesCommand extends CustomCommand {

	public MinigamesCommand(CommandEvent event) {
		super(event);
	}

	WarpService service = new WarpService();

	@Path
	void run() {
		Warp warp = service.get("gamelobby", WarpType.NORMAL);
		if (warp == null) error("There was an error while executing this command");
		warp.teleport(player());
	}

}
